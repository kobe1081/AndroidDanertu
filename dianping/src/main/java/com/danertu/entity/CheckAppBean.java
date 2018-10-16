package com.danertu.entity;

import java.util.List;

/**
 * 作者:  Viz
 * 日期:  2018/8/9 15:46
 * <p>
 * 包名：com.danertu.entity
 * 文件名：CheckAppBean
 * 描述：TODO
 */
public class CheckAppBean {

    private List<ValBean> val;

    public List<ValBean> getVal() {
        return val;
    }

    public void setVal(List<ValBean> val) {
        this.val = val;
    }

    public static class ValBean {
        /**
         * Url : http://115.28.55.222:8018/articlescrap/qy_rule.html
         * Title : 测试强制弹窗
         * IsForced : 0
         */

        private String Url;
        private String Title;
        private String IsForced;

        public String getUrl() {
            return Url;
        }

        public void setUrl(String Url) {
            this.Url = Url;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String Title) {
            this.Title = Title;
        }

        public String getIsForced() {
            return IsForced;
        }

        public void setIsForced(String IsForced) {
            this.IsForced = IsForced;
        }
    }
}
