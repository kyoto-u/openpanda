package org.sakaiproject.warehouse.sakai.session;

import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.warehouse.model.SessionBean;
import org.sakaiproject.warehouse.impl.BaseWarehouseTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 12:38:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionWarehouseTask extends BaseWarehouseTask {

   private SqlService sqlService;
   private TimeService timeService;
   protected Collection getItems() {


            // check the db
            String statement = "select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END"
                    + " from SAKAI_SESSION";

            List sessions = sqlService.dbRead(statement, null, new SqlReader()
            {
                public Object readSqlResultRecord(ResultSet result)
                {
                    try
                    {

                        String id = result.getString(1);
                        String server = result.getString(2);
                        String userId = result.getString(3);
                        String ip = result.getString(4);
                        String agent = result.getString(5);
                        Timestamp start = result.getTimestamp(6);
                        Timestamp end = result.getTimestamp(7);

                        SessionBean session = new SessionBean(id, server, userId, ip, agent, start, end);
                        return session;
                    }
                    catch (SQLException ignore)
                    {
                        return null;
                    }
                }
            });

            return sessions;
        }



    public SqlService getSqlService() {
        return sqlService;
    }

    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
    }

    public TimeService getTimeService() {
        return timeService;
    }

    public void setTimeService(TimeService timeService) {
        this.timeService = timeService;
    }
}
