package com.hgups.express.business.dhl.closeout;

import java.util.List;

public class CloseoutResponse {

    /**
     * meta : {"code":200,"timestamp":"2016-07-06T17:05:14-04:00"}
     * data : {"status":{"code":"SUCCESS"},"closeouts":[{"status":{"code":"SUCCESS","numRejected":0,"numAccepted":1,"processTime":1337},"manifests":[{"distributionCenter":"USATL1","manifestId":5555555555555555555,"file":"JVBERi0xLjQKJeLjz9MKNCAwIG9iaiA8PC9Db2xvclNwYWNlL0RldmljZVJ ...","url":"https://webreport.dhlecs.com/shipment/manifests/DriverManifest-F900E7D4-FBBB-B6B3-D8C4A8E084491C71.pdf"}],"closeoutId":99999,"intlFlag":false,"pickup":5300000}]}
     */

    private MetaBean meta;
    private DataBean data;

    @Override
    public String toString() {
        return "CloseoutResponse{" +
                "meta=" + meta +
                ", data=" + data +
                '}';
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class MetaBean {
        /**
         * code : 200
         * timestamp : 2016-07-06T17:05:14-04:00
         */

        private int code;
        private String timestamp;

        @Override
        public String toString() {
            return "MetaBean{" +
                    "code=" + code +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }

    public static class DataBean {
        /**
         * status : {"code":"SUCCESS"}
         * closeouts : [{"status":{"code":"SUCCESS","numRejected":0,"numAccepted":1,"processTime":1337},"manifests":[{"distributionCenter":"USATL1","manifestId":5555555555555555555,"file":"JVBERi0xLjQKJeLjz9MKNCAwIG9iaiA8PC9Db2xvclNwYWNlL0RldmljZVJ ...","url":"https://webreport.dhlecs.com/shipment/manifests/DriverManifest-F900E7D4-FBBB-B6B3-D8C4A8E084491C71.pdf"}],"closeoutId":99999,"intlFlag":false,"pickup":5300000}]
         */

        private StatusBean status;
        private List<CloseoutsBean> closeouts;

        @Override
        public String toString() {
            return "DataBean{" +
                    "status=" + status +
                    ", closeouts=" + closeouts +
                    '}';
        }

        public StatusBean getStatus() {
            return status;
        }

        public void setStatus(StatusBean status) {
            this.status = status;
        }

        public List<CloseoutsBean> getCloseouts() {
            return closeouts;
        }

        public void setCloseouts(List<CloseoutsBean> closeouts) {
            this.closeouts = closeouts;
        }

        public static class StatusBean {
            /**
             * code : SUCCESS
             */

            private String code;

            @Override
            public String toString() {
                return "StatusBean{" +
                        "code='" + code + '\'' +
                        '}';
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }
        }

        public static class CloseoutsBean {
            /**
             * status : {"code":"SUCCESS","numRejected":0,"numAccepted":1,"processTime":1337}
             * manifests : [{"distributionCenter":"USATL1","manifestId":5555555555555555555,"file":"JVBERi0xLjQKJeLjz9MKNCAwIG9iaiA8PC9Db2xvclNwYWNlL0RldmljZVJ ...","url":"https://webreport.dhlecs.com/shipment/manifests/DriverManifest-F900E7D4-FBBB-B6B3-D8C4A8E084491C71.pdf"}]
             * closeoutId : 99999
             * intlFlag : false
             * pickup : 5300000
             */

            private StatusBeanX status;
            private int closeoutId;
            private boolean intlFlag;
            private int pickup;
            private List<ManifestsBean> manifests;

            @Override
            public String toString() {
                return "CloseoutsBean{" +
                        "status=" + status +
                        ", closeoutId=" + closeoutId +
                        ", intlFlag=" + intlFlag +
                        ", pickup=" + pickup +
                        ", manifests=" + manifests +
                        '}';
            }

            public StatusBeanX getStatus() {
                return status;
            }

            public void setStatus(StatusBeanX status) {
                this.status = status;
            }

            public int getCloseoutId() {
                return closeoutId;
            }

            public void setCloseoutId(int closeoutId) {
                this.closeoutId = closeoutId;
            }

            public boolean isIntlFlag() {
                return intlFlag;
            }

            public void setIntlFlag(boolean intlFlag) {
                this.intlFlag = intlFlag;
            }

            public int getPickup() {
                return pickup;
            }

            public void setPickup(int pickup) {
                this.pickup = pickup;
            }

            public List<ManifestsBean> getManifests() {
                return manifests;
            }

            public void setManifests(List<ManifestsBean> manifests) {
                this.manifests = manifests;
            }

            public static class StatusBeanX {
                /**
                 * code : SUCCESS
                 * numRejected : 0
                 * numAccepted : 1
                 * processTime : 1337
                 */

                private String code;
                private int numRejected;
                private int numAccepted;
                private int processTime;

                @Override
                public String toString() {
                    return "StatusBeanX{" +
                            "code='" + code + '\'' +
                            ", numRejected=" + numRejected +
                            ", numAccepted=" + numAccepted +
                            ", processTime=" + processTime +
                            '}';
                }

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public int getNumRejected() {
                    return numRejected;
                }

                public void setNumRejected(int numRejected) {
                    this.numRejected = numRejected;
                }

                public int getNumAccepted() {
                    return numAccepted;
                }

                public void setNumAccepted(int numAccepted) {
                    this.numAccepted = numAccepted;
                }

                public int getProcessTime() {
                    return processTime;
                }

                public void setProcessTime(int processTime) {
                    this.processTime = processTime;
                }
            }

            public static class ManifestsBean {
                /**
                 * distributionCenter : USATL1
                 * manifestId : 5555555555555555555
                 * file : JVBERi0xLjQKJeLjz9MKNCAwIG9iaiA8PC9Db2xvclNwYWNlL0RldmljZVJ ...
                 * url : https://webreport.dhlecs.com/shipment/manifests/DriverManifest-F900E7D4-FBBB-B6B3-D8C4A8E084491C71.pdf
                 */

                private String distributionCenter;
                private long manifestId;
                private String file;
                private String url;

                @Override
                public String toString() {
                    return "ManifestsBean{" +
                            "distributionCenter='" + distributionCenter + '\'' +
                            ", manifestId=" + manifestId +
                            ", file='" + file + '\'' +
                            ", url='" + url + '\'' +
                            '}';
                }

                public String getDistributionCenter() {
                    return distributionCenter;
                }

                public void setDistributionCenter(String distributionCenter) {
                    this.distributionCenter = distributionCenter;
                }

                public long getManifestId() {
                    return manifestId;
                }

                public void setManifestId(long manifestId) {
                    this.manifestId = manifestId;
                }

                public String getFile() {
                    return file;
                }

                public void setFile(String file) {
                    this.file = file;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }
    }
}
