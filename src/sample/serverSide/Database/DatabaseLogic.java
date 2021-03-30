package sample.serverSide.Database;

import com.sun.org.apache.xpath.internal.operations.Bool;
import sample.serverSide.Database.SingletonDatabaseConnection;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseLogic {
    Statement stmt;
    ResultSet rs;
    String sql;

    public DatabaseLogic() {
        try {
            refreshConnect();
            stmt = SingletonDatabaseConnection.getInstance().getConnection().createStatement();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshConnect() {
        try {
            SingletonDatabaseConnection.getInstance().getDBConnection();
            stmt = SingletonDatabaseConnection.getInstance().getConnection().createStatement();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addCodeAnonymusDatabase(String code, String mail) throws NoSuchPaddingException, NoSuchAlgorithmException {
        try {
            refreshConnect();
            rs = stmt.executeQuery("SELECT * FROM activated_unregister");
            while (rs.next()) {
                if (rs.getString("email").equals(mail)) {
                    try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
                        CallableStatement cstmt = conn.prepareCall("{? = CALL del_repeat_unregister_code}");
                        cstmt.setString(1, mail);
                        cstmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    SingletonDatabaseConnection.getInstance().getDBConnection();
                    stmt = SingletonDatabaseConnection.getInstance().getConnection().createStatement();
                }
            }
            try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
                CallableStatement cstmt = conn.prepareCall("{? = CALL test(?)}");
                cstmt.setString(1, mail);
                cstmt.setString(2, code);
                cstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        refreshConnect();
        return true;
    }
    /*Вернет true если в таблице незарегестрированных пользователей код соответствует нужному мейлу*/
    public boolean checkUserUnregisterCode(String code, String mail) {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL checkcode(?)}");
            cstmt.setString(1, mail);
            cstmt.setString(2, code);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();
            boolean isOk = cstmt.getBoolean(1);
            return isOk;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkMailIsUnregister(String mail)
            throws NoSuchPaddingException, NoSuchAlgorithmException {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL check_mail_unregister}");
            cstmt.setString(1, mail);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();
            boolean canRegisterThisMail = cstmt.getBoolean(1);
            if (canRegisterThisMail) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUserCode(String code, String mail) {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL checkcode(?)}");
            cstmt.setString(1, mail);
            cstmt.setString(2, code);
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.executeUpdate();
            String answer = cstmt.getString(1);
            if (answer == null) return false;
            if (answer.equals(code)) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUserActivatedCode(String code, String login) {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL checkcode_activated(?)}");
            cstmt.setString(1, login);
            cstmt.setString(2, code);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();
            boolean isCodeEquals = cstmt.getBoolean(1);
            if (isCodeEquals) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUser(String login, String pswd) {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL checkUser(?)}");
            cstmt.setString(1, login);
            cstmt.setString(2, pswd);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();
            boolean ok = cstmt.getBoolean(1);
            if (ok) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }
/*
    public boolean getFriend(String login, FriendsList fl) {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            PreparedStatement ps = SingletonDatabaseConnection.getInstance().getDBConnection().prepareStatement(" SELECT id_friend,status FROM contacts WHERE id_user = ( SELECT id_user FROM users WHERE user_name=?)");
            ps.setString(1, login);
            ArrayList<Integer> id_friend = new ArrayList<Integer>();
            ArrayList<String> status = new ArrayList<String>();
            ArrayList<String> name_friends = new ArrayList<String>();
            ArrayList<Boolean> statusOnline = new ArrayList<>();
            rs = ps.executeQuery();
            while (rs.next()) {
                id_friend.add(rs.getInt("id_friend"));
                status.add(rs.getString("status"));
            }
            int i = 0;
            do {
                PreparedStatement ps1 = SingletonDatabaseConnection.getInstance().
                        getDBConnection().prepareStatement(" SELECT user_name FROM users WHERE id_user = ?");
                ps1.setInt(1, id_friend.get(i));
                rs = ps1.executeQuery();
                while (rs.next()) {
                    name_friends.add(rs.getString("user_name"));
                }
                i++;
            } while (i < id_friend.size());
            i = 0;
            do {
                CallableStatement cstmt = conn.prepareCall("{? = CALL user_now_is_online}");
                cstmt.setString(1, name_friends.get(i));
                cstmt.registerOutParameter(1, Types.BOOLEAN);
                cstmt.execute();
                statusOnline.add(cstmt.getBoolean(1));
                i++;
            } while (i < id_friend.size());
            fl.setStatusOnline(statusOnline);
            fl.setFriend_name(name_friends);
            fl.setStatus(status);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

*/
    public boolean setOnline(String login) {
        refreshConnect();


        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL update_online}");
            cstmt.setString(1, login);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    public boolean changePswd(String login, String newPswd) {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL update_user_pswd (?)}");
            cstmt.setString(1, login);
            cstmt.setString(2, newPswd);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUserLoginFromMail(String mail) {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL get_user_login_from_mail}");
            cstmt.setString(1, mail);
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.executeUpdate();
            String answer = cstmt.getString(1);
            return answer;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean updateFriendStatus(String userName, String friendName, byte statusFrom, byte StatusTO) {
        refreshConnect();
        try {
            Connection conn = SingletonDatabaseConnection.getInstance().getConnection();
            CallableStatement cstmt = conn.prepareCall("{?= CALL addFriendRequest(?,?,?) }");
            cstmt.setString(1, userName);
            cstmt.setString(2, friendName);
            cstmt.setByte(3, statusFrom);
            cstmt.setByte(4, StatusTO);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();

        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean friendManipulator(String userName, String friendName, byte status) {
        refreshConnect();
        try {
            Connection conn = SingletonDatabaseConnection.getInstance().getConnection();
            CallableStatement cstmt = conn.prepareCall("{?= CALL addfriend(?,?) }");
            cstmt.setString(1, userName);
            cstmt.setString(2, friendName);
            cstmt.setByte(3, status);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }
/*
    public boolean getUsersSearch(String userLogin, String name, UsersSearch search) {
        ArrayList<String> users = new ArrayList<String>();
        ArrayList<Integer> users_id = new ArrayList<Integer>();
        ArrayList<String> status = new ArrayList<String>();
        ArrayList<Boolean> statusOnline = new ArrayList<>();
        int idUser;
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL searchnewuser}");
            cstmt.setString(1, name);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                if (!rs.getString("user_name").equals(userLogin))
                    users.add(rs.getString("user_name"));
            }
            cstmt = conn.prepareCall("{?= CALL get_id_user_from_login }");
            cstmt.setString(1, userLogin);
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.execute();
            idUser = cstmt.getInt(1);
            int i = 0;
            do {
                cstmt = conn.prepareCall("{?= CALL get_id_user_from_login }");
                cstmt.setString(1, users.get(i));
                cstmt.registerOutParameter(1, Types.INTEGER);
                cstmt.execute();
                users_id.add(cstmt.getInt(1));
                i++;
            } while (i < users.size());
            i = 0;
            do {
                try {
                    cstmt = conn.prepareCall("{?= CALL get_status_from_id(?) }");
                    cstmt.setInt(1, idUser);
                    cstmt.setInt(2, users_id.get(i));
                    cstmt.registerOutParameter(1, Types.SMALLINT);
                    cstmt.execute();
                    status.add(String.valueOf(cstmt.getShort(1)));
                } catch (SQLException e) {
                    status.add("0");
                }
                i++;
            } while (i < users.size());
            i = 0;
            do {
                cstmt = conn.prepareCall("{? = CALL user_now_is_online}");
                cstmt.setString(1, users.get(i));
                cstmt.registerOutParameter(1, Types.BOOLEAN);
                cstmt.execute();
                statusOnline.add(cstmt.getBoolean(1));
                i++;
            } while (i < users.size());
            search.setStatus(status);
            search.setUser_name(users);
            search.setStatusOnline(statusOnline);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return false;
        }
        return false;
    }
*/
    public boolean registrationUser(String code, String login, String pswd) {
        refreshConnect();
        try {
            rs = stmt.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                if (rs.getString("user_name").equals(login)) {
                    return false;
                }
            }
            try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
                CallableStatement cstmt = conn.prepareCall("{? = CALL addnewuser(?)}");
                cstmt.setString(1, login);
                cstmt.setString(2, pswd);
                cstmt.execute();
                cstmt.cancel();
                cstmt = conn.prepareCall("{?= CALL get_id_user_from_login }");
                cstmt.setString(1, login);
                cstmt.registerOutParameter(1, Types.INTEGER);
                cstmt.execute();
                int idUser = cstmt.getInt(1);
                cstmt = conn.prepareCall("{?= CALL email_unregister_from_code }");
                cstmt.setString(1, code);
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.execute();
                String mailuser = cstmt.getString(1);
                cstmt = conn.prepareCall("{?= CALL from_unregister_to_register(?,?) }");
                cstmt.setInt(1, idUser);
                cstmt.setString(2, mailuser);
                cstmt.setString(3, code);
                cstmt.execute();
                cstmt = conn.prepareCall("{? = CALL del_repeat_unregister_code}");
                cstmt.setString(1, mailuser);
                cstmt.execute();
                cstmt = conn.prepareCall("{? = CALL add_online}");
                cstmt.setString(1, login);
                cstmt.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String checkLogin(String login) throws NoSuchPaddingException, NoSuchAlgorithmException {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL get_user_mail_from_login}");
            cstmt.setString(1, login);
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.execute();
            String user_mail = cstmt.getString(1);
            if (user_mail == null) return "";
            return user_mail;
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean upadteCodeActivated(String mail, String code) throws NoSuchPaddingException, NoSuchAlgorithmException {
        refreshConnect();
        try (Connection conn = SingletonDatabaseConnection.getInstance().getConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL update_code_activated(?)}");
            cstmt.setString(1, mail);
            cstmt.setString(2, code);
            cstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}