package com.danertu.entity;

import java.util.List;

public class LeaderBean {

    /**
     * LeaderInfo : {"LeaderBean":[{"ShopName":"ck醇康","memberid":"chunkang","Mobile":"4009952220","RealName":"醇康贸易"}]}
     */

    private LeaderInfoBean LeaderInfo;

    public LeaderInfoBean getLeaderInfo() {
        return LeaderInfo;
    }

    public void setLeaderInfo(LeaderInfoBean LeaderInfo) {
        this.LeaderInfo = LeaderInfo;
    }

    public static class LeaderInfoBean {
        private List<LeaderBeanBean> LeaderBean;

        public List<LeaderBeanBean> getLeaderBean() {
            return LeaderBean;
        }

        public void setLeaderBean(List<LeaderBeanBean> LeaderBean) {
            this.LeaderBean = LeaderBean;
        }

        public static class LeaderBeanBean {
            /**
             * ShopName : ck醇康
             * memberid : chunkang
             * Mobile : 4009952220
             * RealName : 醇康贸易
             */

            private String ShopName;
            private String memberid;
            private String Mobile;
            private String RealName;

            public String getShopName() {
                return ShopName;
            }

            public void setShopName(String ShopName) {
                this.ShopName = ShopName;
            }

            public String getMemberid() {
                return memberid;
            }

            public void setMemberid(String memberid) {
                this.memberid = memberid;
            }

            public String getMobile() {
                return Mobile;
            }

            public void setMobile(String Mobile) {
                this.Mobile = Mobile;
            }

            public String getRealName() {
                return RealName;
            }

            public void setRealName(String RealName) {
                this.RealName = RealName;
            }
        }
    }
}
