package com.hgups.express.business.dhl.closeout;

import java.util.List;

public class CloseoutParameter {


    private List<CloseoutRequestsBean> closeoutRequests;

    @Override
    public String toString() {
        return "CloseoutParameter{" +
                "closeoutRequests=" + closeoutRequests +
                '}';
    }

    public List<CloseoutRequestsBean> getCloseoutRequests() {
        return closeoutRequests;
    }

    public void setCloseoutRequests(List<CloseoutRequestsBean> closeoutRequests) {
        this.closeoutRequests = closeoutRequests;
    }

    public static class CloseoutRequestsBean {
        private List<PackagesBean> packages;

        @Override
        public String toString() {
            return "CloseoutRequestsBean{" +
                    "packages=" + packages +
                    '}';
        }

        public List<PackagesBean> getPackages() {
            return packages;
        }

        public void setPackages(List<PackagesBean> packages) {
            this.packages = packages;
        }

        public static class PackagesBean {
            /**
             * packageId : 99999999999999999999999999999
             */

            private String packageId;

            @Override
            public String toString() {
                return "PackagesBean{" +
                        "packageId='" + packageId + '\'' +
                        '}';
            }

            public String getPackageId() {
                return packageId;
            }

            public void setPackageId(String packageId) {
                this.packageId = packageId;
            }
        }
    }
}
