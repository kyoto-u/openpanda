package org.sakaiproject.warehouse.sakai.user;

import org.sakaiproject.db.api.SqlReader;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.warehouse.model.UserBean;
import org.sakaiproject.warehouse.impl.BaseWarehouseTask;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Dec 19, 2005
 * Time: 12:38:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserWarehouseTask extends BaseWarehouseTask {

   private SqlService sqlService;
   private TimeService timeService;
   protected Collection getItems() {


            // check the db
            String statement = "Select a.USER_ID, b.EID, a.EMAIL, a.EMAIL_LC, a.FIRST_NAME, a.LAST_NAME, a.TYPE  from SAKAI_USER a, SAKAI_USER_ID_MAP b where a.USER_ID=b.USER_ID";


            List users = sqlService.dbRead(statement, null, new SqlReader()
            {
                public Object readSqlResultRecord(ResultSet result)
                {
                    try
                    {

                        String userId = result.getString(1);
                        String userEid = result.getString(2);
                        String email = result.getString(3);
                        String emailLc = result.getString(4);
                        String firstName = result.getString(5);
                        String  lastName = result.getString(6);
                        String type = result.getString(7);

                        UserBean user = new UserBean(userId, userEid, email, emailLc, firstName, lastName, type);
                        return user;
                    }
                    catch (SQLException ignore)
                    {
                        return null;
                    }
                }
            });

            return users;
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
