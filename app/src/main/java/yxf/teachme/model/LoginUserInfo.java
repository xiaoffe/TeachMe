package yxf.teachme.model;

/**
 * Created by Administrator on 2017/7/14.
 */

public class LoginUserInfo {


    /**
     * result : 0
     * msg : Success
     * data : {"mobile":"13652895087","nickname":"李老师","invite":"931324","name":"李伟大","avatar":"http://127.0.0.1/Api/Upload/avatar/20151129/77d1beb843ca41da7ee1def0d5271a56.j pg","member":"1","login_id":"20be8a1bd18a3338f43ee7de6c93c4f1"}
     */

    private String result;
    private String msg;
    /**
     * mobile : 13652895087
     * nickname : 李老师
     * invite : 931324
     * name : 李伟大
     * avatar : http://127.0.0.1/Api/Upload/avatar/20151129/77d1beb843ca41da7ee1def0d5271a56.jpg
     * member : 1
     * login_id : 20be8a1bd18a3338f43ee7de6c93c4f1
     */

    public DataEntity data;

    public void setResult(String result) {
        this.result = result;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        private String mobile;
        private String nickname;
        private String invite;
        private String examine;
        private String msg;
        private String avatar;
        //用户的类型，0为学生，1为老师
        private String member;
        private String login_id;

        @Override
        public String toString() {
            return "DataEntity{" +
                    "mobile='" + mobile + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", invite='" + invite + '\'' +
                    ", examine='" + examine + '\'' +
                    ", msg='" + msg + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", member='" + member + '\'' +
                    ", login_id='" + login_id + '\'' +
                    '}';
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setInvite(String invite) {
            this.invite = invite;
        }

        public void setExamine(String examine) {
            this.examine = examine;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setMember(String member) {
            this.member = member;
        }

        public void setLogin_id(String login_id) {
            this.login_id = login_id;
        }

        public String getMobile() {
            return mobile;
        }

        public String getNickname() {
            return nickname;
        }

        public String getInvite() {
            return invite;
        }

        public String getExamine() {
            return examine;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getMember() {
            return member;
        }

        public String getLogin_id() {
            return login_id;
        }
    }

    @Override
    public String toString() {
        return "LoginUserInfo{" +
                "result='" + result + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
