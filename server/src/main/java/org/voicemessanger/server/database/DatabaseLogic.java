package org.voicemessanger.server.database;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.voicemessanger.server.main.CloudinaryConfig;
import org.voicemessanger.server.main.FriendsHelper;
import org.voicemessanger.server.main.ImageTransformer;


import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DatabaseLogic {
    Statement stmt;
    ResultSet rs;
    String sql;
    DatabaseConnection databaseConnection;

    public DatabaseLogic() {
        try {
            
            databaseConnection = new DatabaseConnection();
            stmt = databaseConnection. getDBConnection().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

  

    public boolean addCodeAnonymusDatabase(String code, String mail) throws NoSuchPaddingException, NoSuchAlgorithmException {
        try {
            
            rs = stmt.executeQuery("SELECT * FROM activated_unregister");
            while (rs.next()) {
                if (rs.getString("email").equals(mail)) {
                    try (Connection conn = databaseConnection. getDBConnection()) {
                        CallableStatement cstmt = conn.prepareCall("{? = CALL del_repeat_unregister_code}");
                        cstmt.setString(1, mail);
                        cstmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            try (Connection conn = databaseConnection. getDBConnection()) {
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
        // 
        // DatabaseConnection.getInstance().closeConncetion();
        return true;
    }

    /*???????????? true ???????? ?? ?????????????? ???????????????????????????????????????? ?????????????????????????? ?????? ?????????????????????????? ?????????????? ??????????*/
    public boolean checkUserUnregisterCode(String code, String mail) {
        
        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL checkcode(?)}");
            cstmt.setString(1, mail);
            cstmt.setString(2, code);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();
            boolean isOk = cstmt.getBoolean(1);
            //DatabaseConnection.getInstance().closeConncetion();

            return isOk;
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return false;
    }

    public boolean checkMailIsUnregister(String mail)
            throws NoSuchPaddingException, NoSuchAlgorithmException {
        
        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL check_mail_unregister}");
            cstmt.setString(1, mail);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();

            boolean canRegisterThisMail = cstmt.getBoolean(1);
            //DatabaseConnection.getInstance().closeConncetion();

            if (canRegisterThisMail) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUserCode(String code, String mail) {

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL checkcode(?)}");
            cstmt.setString(1, mail);
            cstmt.setString(2, code);
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.executeUpdate();
            String answer = cstmt.getString(1);
            //DatabaseConnection.getInstance().closeConncetion();

            if (answer == null) return false;
            if (answer.equals(code)) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUserActivatedMailCode(String code, String mail) {

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL checkcode_active_mail(?)}");
            cstmt.setString(1, mail);
            cstmt.setString(2, code);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();
            boolean isCodeEquals = cstmt.getBoolean(1);
            //DatabaseConnection.getInstance().closeConncetion();

            return isCodeEquals;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkUserActivatedCode(String code, String login) {

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL checkcode_activated(?)}");
            cstmt.setString(1, login);
            cstmt.setString(2, code);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();
            boolean isCodeEquals = cstmt.getBoolean(1);
            //DatabaseConnection.getInstance().closeConncetion();

            if (isCodeEquals) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getAvatarFromUsername(String username) {

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL get_img_url_login}");
            cstmt.setString(1, username);
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.executeUpdate();
            String answer = cstmt.getString(1);
            //DatabaseConnection.getInstance().closeConncetion();

            if (answer == null) return "default";
            if (answer.equals("")) return "default";
            return answer;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "default";
    }

    public boolean checkUser(String login, String pswd) {

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL checkUser(?)}");
            cstmt.setString(1, login);
            cstmt.setString(2, pswd);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();
            boolean ok = cstmt.getBoolean(1);
            //DatabaseConnection.getInstance().closeConncetion();

            if (ok) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
        public boolean getFriend(String login, FriendsList fl) {

            try (Connection conn = databaseConnection. getDBConnection()) {
                PreparedStatement ps = DatabaseConnection.getInstance().getDBConnection().prepareStatement(" SELECT id_friend,status FROM contacts WHERE id_user = ( SELECT id_user FROM users WHERE user_name=?)");
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
                    PreparedStatement ps1 = DatabaseConnection.getInstance().
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



        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL update_online}");
            cstmt.setString(1, login);
            cstmt.execute();
            //DatabaseConnection.getInstance().closeConncetion();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }catch (Exception e) {
            return false;
        }
        return false;
    }


    public boolean changePswd(String login, String newPswd) {

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL update_user_pswd (?)}");
            cstmt.setString(1, login);
            cstmt.setString(2, newPswd);
            cstmt.execute();
            //DatabaseConnection.getInstance().closeConncetion();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUserLoginFromMail(String mail) {

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL get_user_login_from_mail}");
            cstmt.setString(1, mail);
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.executeUpdate();
            String answer = cstmt.getString(1);
            //DatabaseConnection.getInstance().closeConncetion();

            return answer;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean updateFriendStatus(String userName, String friendName, byte statusFrom, byte StatusTO) {

        try {
            Connection conn = databaseConnection. getDBConnection();
            CallableStatement cstmt = conn.prepareCall("{?= CALL addFriendRequest(?,?,?) }");
            cstmt.setString(1, userName);
            cstmt.setString(2, friendName);
            cstmt.setByte(3, statusFrom);
            cstmt.setByte(4, StatusTO);
            cstmt.execute();
            cstmt.close();
            conn.close();
            //DatabaseConnection.getInstance().closeConncetion();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }

    public boolean friendManipulator(String userName, String friendName, byte status) {

        try {
            Connection conn = databaseConnection. getDBConnection();
            CallableStatement cstmt = conn.prepareCall("{?= CALL addfriend(?,?) }");
            cstmt.setString(1, userName);
            cstmt.setString(2, friendName);
            cstmt.setByte(3, status);
            cstmt.execute();
            cstmt.close();
            conn.close();
            //DatabaseConnection.getInstance().closeConncetion();

            return true;
        } catch (SQLException e) {
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

        try (Connection conn = databaseConnection. getDBConnection()) {
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

    public boolean changeAvatar(String login, String newAvatar) {

        try (Connection conn = databaseConnection. getDBConnection()) {

            Map configImg = new HashMap();
            CloudinaryConfig confCloud = new CloudinaryConfig();
            configImg.put("cloud_name", confCloud.getCloud_name());
            configImg.put("api_key", confCloud.getApi_key());
            configImg.put("api_secret", confCloud.getApi_secret());

            Cloudinary cloudinary = new Cloudinary(configImg);

            CallableStatement cstmt = conn.prepareCall("{? = CALL get_img_url_login}");
            cstmt.setString(1, login);
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.execute();
            String user_img_public_id = cstmt.getString(1);
            if (!user_img_public_id.equals("default")) {
                cloudinary.uploader().destroy(user_img_public_id, ObjectUtils.emptyMap());
            }


            cstmt = conn.prepareCall("{? = CALL update_img_url_login(?)}");
            cstmt.setString(1, login);
            cstmt.setString(2, newAvatar);
            cstmt.execute();
            //DatabaseConnection.getInstance().closeConncetion();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean loadAvatarToCloud(BufferedImage buffImage, String login) {
        CloudinaryConfig cloudinaryConfig = new CloudinaryConfig();
        Map config = new HashMap();
        Map load_img_info = null;
        config.put("cloud_name", cloudinaryConfig.getCloud_name());
        config.put("api_key", cloudinaryConfig.getApi_key());
        config.put("api_secret", cloudinaryConfig.getApi_secret());
        Cloudinary cloudinary = new Cloudinary(config);
        //https://res.cloudinary.com/diplomaimgdpi/image/upload/k2tx9q47isouxxfnvdbr.png
        File outputfile = new File("./temp_" + login + ".png");

        try {
            ImageIO.write(buffImage, "png", outputfile);
            load_img_info = cloudinary.uploader().upload(outputfile.getPath(), ObjectUtils.emptyMap());
            outputfile.delete();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("?????????????????? ?????????????? ?????? ???????????????? ?? ?????????? " + login);
        System.out.println("URL: " + load_img_info.get("public_id"));

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL insert_img_url_login(?)}");
            cstmt.setString(1, login);
            cstmt.setString(2, (String) load_img_info.get("public_id"));
            cstmt.execute();
            // DatabaseConnection.getInstance().closeConncetion();

            System.out.println("???????????????? ???????? ??????????????????, ?????????????????? ??????");

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void createFirstAvatar(String login) {
        URLConnection uc = null;
        URL url = null;
        InputStream urlStream = null;
        try {
            url = new URL("https://api.multiavatar.com/" + login + ".png");
            uc = url.openConnection();
            uc.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            uc.connect();
            urlStream = uc.getInputStream();
            BufferedImage image1 = ImageIO.read(urlStream);
            image1 = new ImageTransformer().scale(image1, 50, 50);
            if (loadAvatarToCloud(image1, login) == false) {
                System.out.println("[????????????] ???? ?????????????? ?????????????? ?????????????? ?????? ????????????????????????1");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("[????????????] ???? ?????????????? ?????????????? ?????????????? ?????? ????????????????????????2");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[????????????] ???? ?????????????? ?????????????? ?????????????? ?????? ????????????????????????3");
        }
    }

    public boolean registrationUser(String code, String login, String pswd) {


        try (Connection conn = databaseConnection. getDBConnection()) {

            CallableStatement cstmt = conn.prepareCall("{? = CALL check_free_username}");
            cstmt.setString(1, login);
            cstmt.registerOutParameter(1, Types.BOOLEAN);
            cstmt.execute();
            boolean isFreeusername = cstmt.getBoolean(1);
            if (!isFreeusername) return false;

            cstmt = conn.prepareCall("{? = CALL addnewuser(?)}");
            cstmt.setString(1, login);
            cstmt.setString(2, pswd);
            cstmt.execute();
            //cstmt.cancel();

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

            createFirstAvatar(login);
            // DatabaseConnection.getInstance().closeConncetion();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String checkLogin(String login) throws NoSuchPaddingException, NoSuchAlgorithmException {

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL get_user_mail_from_login}");
            cstmt.setString(1, login);
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.execute();
            String user_mail = cstmt.getString(1);
            // DatabaseConnection.getInstance().closeConncetion();

            if (user_mail == null) return "";
            return user_mail;
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean upadteCodeActivated(String mail, String code) throws NoSuchPaddingException, NoSuchAlgorithmException {

        try (Connection conn = databaseConnection. getDBConnection()) {
            CallableStatement cstmt = conn.prepareCall("{? = CALL update_code_activated(?)}");
            cstmt.setString(1, mail);
            cstmt.setString(2, code);
            cstmt.execute();
            //DatabaseConnection.getInstance().closeConncetion();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean getFriend(String login, FriendsHelper frh) {
        System.out.println("[????????????] ?????????????? ???????????? ???? ????");

        try (Connection conn = databaseConnection. getDBConnection()) {
            PreparedStatement ps = conn.prepareStatement(" SELECT id_friend,status FROM contacts WHERE id_user = ( SELECT id_user FROM users WHERE user_name=?)");
            ps.setString(1, login);
            ArrayList<Integer> id_friend = new ArrayList<Integer>();
            ArrayList<String> status = new ArrayList<String>();
            ArrayList<String> name_friends = new ArrayList<String>();
            ArrayList<Boolean> statusOnline = new ArrayList<>();
            ArrayList<String> imgFriend = new ArrayList<>();

            rs = ps.executeQuery();
            while (rs.next()) {
                id_friend.add(rs.getInt("id_friend"));
                status.add(rs.getString("status"));
            }
            int i = 0;
            if (id_friend.size() > 0) {


                PreparedStatement ps1 = conn.prepareStatement(" SELECT user_name FROM users WHERE id_user = ?");
                do {
                    ps1.setInt(1, id_friend.get(i));
                    rs = ps1.executeQuery();
                    while (rs.next()) {
                        name_friends.add(rs.getString("user_name"));
                    }
                    i++;
                } while (i < id_friend.size());
                i = 0;

                Connection conn1 = databaseConnection. getDBConnection();
                CallableStatement cstmt = conn1.prepareCall("{? = CALL user_now_is_online}");
                do {
                    cstmt.setString(1, name_friends.get(i));
                    cstmt.registerOutParameter(1, Types.BOOLEAN);
                    cstmt.execute();
                    statusOnline.add(cstmt.getBoolean(1));
                    i++;
                } while (i < id_friend.size());
                i = 0;
                cstmt = conn1.prepareCall("{? = CALL get_img_url_login}");
                do {
                    cstmt.setString(1, name_friends.get(i));
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.execute();
                    String img = cstmt.getString(1);
                    if (img == null) {
                        imgFriend.add("default");
                    } else {
                        imgFriend.add(img);
                    }
                    i++;
                } while (i < name_friends.size());
                frh.setStatusOnline(statusOnline);
                frh.setFriend_name(name_friends);
                frh.setStatus(status);
                frh.setAvatars(imgFriend);
                conn1.close();
            }
            //DatabaseConnection.getInstance().closeConncetion();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }
}