package com.huawei.vcenterpluginui.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huawei.vcenterpluginui.constant.SyncStatus;
import com.huawei.vcenterpluginui.entity.Task;

public class TaskDao extends H2DataBaseDao {

	public List<Task> getIncompletedTaskList(Map<String, Object> param) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			StringBuffer sql = new StringBuffer();
			sql.append("select * from HW_ESIGHT_TASK WHERE HW_ESIGHT_HOST_ID in (select id from HW_ESIGHT_HOST )");

			if(param.containsKey("esightIp")){
				sql.append(" and exists (select 1 from HW_ESIGHT_HOST WHERE Host_IP =? and HW_ESIGHT_TASK.HW_ESIGHT_HOST_ID=HW_ESIGHT_HOST.ID) ");
			}

			if (param.containsKey("taskType")) {
				sql.append(" and TASK_TYPE = ?");
			}
			if (param.containsKey("incompleted")) {
				sql.append(" and (TASK_PROGRESS < 100 or SYNC_STATUS = '" + SyncStatus.STATUS_CREATED + "')");
			}			
			if (param.containsKey("unSuccess")) {
				sql.append(" and SYNC_STATUS <> '" + SyncStatus.STATUS_FINISHED + "'");
			}
			if (param.containsKey("taskName")) {
				sql.append(" and TASK_NAME like ?");
			}
			if (param.containsKey("softwareSourceName")) {
				sql.append(" and SOFTWARE_SOURCE_NAME like ?");
			}
			if (param.containsKey("taskStatus")) {
				sql.append(" and SYNC_STATUS = ?");
			}
			if (param.containsKey("order")) {
				if ("taskName".equals(param.get("order").toString())) {
					sql.append(" order by TASK_NAME");
				} else if ("syncStatus".equals(param.get("order").toString())) {
					sql.append(" order by SYNC_STATUS");
				} else if ("taskProgress".equals(param.get("order").toString())) {
					sql.append(" order by TASK_PROGRESS");
				} else if ("taskDate".equals(param.get("order").toString())) {
					sql.append(" order by CREATE_TIME");
				} else if ("softwareSourceName".equals(param.get("order").toString())) {
					sql.append(" order by SOFTWARE_SOURCE_NAME");
				}else if ("createTime".equals(param.get("order").toString())) {
					sql.append(" order by CREATE_TIME");
				}

				if (!"false".equals(param.get("orderDesc").toString())) {
					sql.append(" desc");
				}
			}
			if (param.containsKey("pageNo")) {
				sql.append(" limit ? offset ?");
			}
			
			LOGGER.info(sql.toString());
			ps = con.prepareStatement(sql.toString());

			int i = 1;

			if (param.containsKey("esightIp")) {
				ps.setString(i++, param.get("esightIp").toString());
			}

			if (param.containsKey("taskType")) {
				ps.setString(i++, param.get("taskType").toString());
			}

			if (param.containsKey("taskName")) {
				ps.setString(i++, "%" + param.get("taskName").toString() + "%");
			}
			
			if (param.containsKey("softwareSourceName")) {
				ps.setString(i++, "%" + param.get("softwareSourceName").toString() + "%");
			}

			if (param.containsKey("taskStatus")) {
				ps.setString(i++, param.get("taskStatus").toString());
			}

			if (param.containsKey("pageNo")) {
				int pageNo = (int) param.get("pageNo");
				int pageSize = (int) param.get("pageSize");
				ps.setInt(i++, pageSize);
				ps.setInt(i++, (pageNo - 1) * pageSize);
			}
			rs = ps.executeQuery();
			List<Task> taskList = new ArrayList<>();
			while (rs.next()) {
				Task task = new Task();
				task.setId(rs.getInt("ID"));
				task.setHwEsighthostId(rs.getInt("HW_ESIGHT_HOST_ID"));
				task.setTaskName(rs.getString("TASK_NAME"));
				task.setSoftwareSourceName(rs.getString("SOFTWARE_SOURCE_NAME"));
				task.setTemplates(rs.getString("TEMPLATES"));
				task.setDeviceIp(rs.getString("DEVICE_IP"));
				task.setTaskStatus(rs.getString("TASK_STATUS"));
				task.setReservedStr1(rs.getString("RESERVED_STR1"));
				task.setSyncStatus(rs.getString("SYNC_STATUS"));
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				task.setCreateTime(sdf.format(rs.getTimestamp("CREATE_TIME")));
				task.setLastModify(sdf.format(rs.getTimestamp("LAST_MODIFY_TIME")));
				task.setTaskType(rs.getString("TASK_TYPE"));
				task.setTaskProgress(rs.getInt("TASK_PROGRESS"));
				task.setErrorDetail(rs.getString("ERROR_DETAIL"));
				task.setTaskCode(rs.getString("TASK_CODE"));
				task.setTaskResult(rs.getString("TASK_RESULT"));

				taskList.add(task);
			}
			return taskList;
		} catch (SQLException e) {
			LOGGER.error(e);
			throw e;
		} finally {
			closeConnection(con, ps, rs);
		}
	}
	
	public int getCountTaskList(Map<String, Object> param) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			StringBuffer sql = new StringBuffer();
			sql.append("select count(*) as count from HW_ESIGHT_TASK WHERE HW_ESIGHT_HOST_ID in (select id from HW_ESIGHT_HOST)");

			if(param.containsKey("esightIp")){
				sql.append(" and exists (select 1 from HW_ESIGHT_HOST WHERE Host_IP =? and HW_ESIGHT_TASK.HW_ESIGHT_HOST_ID=HW_ESIGHT_HOST.ID) ");
			}

			if (param.containsKey("taskType")) {
				sql.append(" and TASK_TYPE = ?");
			}
			if (param.containsKey("incompleted")) {
				if ((Boolean) param.get("incompleted")) {
					sql.append(" and TASK_PROGRESS < 100");
				}
			}
			if (param.containsKey("taskName")) {
				sql.append(" and SOFTWARE_SOURCE_NAME like ?");
			}
			if (param.containsKey("taskStatus")) {
				sql.append(" and SYNC_STATUS = ?");
			}
			ps = con.prepareStatement(sql.toString());

			int i = 1;
			if (param.containsKey("esightIp")) {
				ps.setString(i++, param.get("esightIp").toString());
			}
			if (param.containsKey("taskType")) {
				ps.setString(i++, param.get("taskType").toString());
			}
			if (param.containsKey("taskName")) {
				ps.setString(i++, "%" + param.get("taskName").toString() + "%");
			}

			if (param.containsKey("taskStatus")) {
				ps.setString(i++, param.get("taskStatus").toString());
			}

			rs = ps.executeQuery();
			int count = 0;
			if (rs.next()) {
				count = rs.getInt("count");
			}
			return count;
		} catch (SQLException e) {
			LOGGER.error(e);
			throw e;
		} finally {
			closeConnection(con, ps, rs);
		}
	}

	public int saveTask(Task task) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(
					"insert into HW_ESIGHT_TASK (HW_ESIGHT_HOST_ID,TASK_NAME,SOFTWARE_SOURCE_NAME,SYNC_STATUS,TASK_STATUS,TASK_PROGRESS,TASK_TYPE,RESERVED_STR1,DEVICE_IP,CREATE_TIME, LAST_MODIFY_TIME) values (?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)");
			ps.setInt(1, task.getHwEsighthostId());
			ps.setString(2, task.getTaskName());
			ps.setString(3, task.getSoftwareSourceName());
			ps.setString(4, task.getSyncStatus());
			ps.setString(5, task.getTaskStatus());
			ps.setInt(6, task.getTaskProgress());
			ps.setString(7, task.getTaskType());
			ps.setString(8, task.getReservedStr1());
			ps.setString(9, task.getDeviceIp());
			return ps.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error(e);
			throw e;
		} finally {
			closeConnection(con, ps, null);
		}

	}

	public int saveTaskStatus(Task task) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement("update HW_ESIGHT_TASK set SYNC_STATUS = ?,TASK_PROGRESS = ?,TASK_RESULT = ?,TASK_CODE = ?,ERROR_DETAIL = ?, LAST_MODIFY_TIME = CURRENT_TIMESTAMP where ID = ?");
			ps.setString(1, task.getSyncStatus());
			ps.setInt(2, task.getTaskProgress());
			ps.setString(3, task.getTaskResult());
			ps.setString(4, task.getTaskCode());
			ps.setString(5, task.getErrorDetail());
			ps.setInt(6, task.getId());
			return ps.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error(e);
			throw e;
		} finally {
			closeConnection(con, ps, null);
		}
	}

	public int deleteFailedTask(String taskType) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(
					"delete from HW_ESIGHT_TASK where (SYNC_STATUS = '" + SyncStatus.STATUS_HW_FAILED + "' or SYNC_STATUS = '" + SyncStatus.STATUS_SYNC_FAILED + "' or SYNC_STATUS = '" + SyncStatus.STATUS_HW_PARTION_FAILED + "') and TASK_TYPE = ?");
			ps.setString(1, taskType);
			return ps.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error(e);
			throw e;
		} finally {
			closeConnection(con, ps, null);
		}
	}

	public int deleteTaskById(int taskId) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = getConnection();
			ps = con.prepareStatement("delete from HW_ESIGHT_TASK where ID = ?");
			ps.setInt(1, taskId);

			return ps.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error(e);
			throw e;
		} finally {
			closeConnection(con, ps, null);
		}
	}
}
