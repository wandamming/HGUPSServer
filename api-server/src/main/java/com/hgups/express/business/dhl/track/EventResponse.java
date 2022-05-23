package com.hgups.express.business.dhl.track;

import java.util.List;

public class EventResponse {

    /**
     * meta : {"code":200,"timestamp":"2020-11-10T00:52:59-05:00"}
     * data : {"status":{"numRejected":0,"numAccepted":1},"mailItems":[{"events":[{"location":"Atlanta, GA","zip":"","DHLGMEVENTID":110,"longitude":"","attemptCount":1,"state":"","eventId":"CE108110","time":"12:01:30","hubCode":"","eventDescription":"Doing pickup","delivery":{"secondaryEventDescription":"","secondaryEventId":"","originalAmount":"","signedByName":"","moneyTransactionType":"","remarks":"String Content","actualAmount":"","signedByImage":"","codCollected":""},"date":"11/11/2020","eventClass":"PICKUP","country":"US","container":"String Content","timezone":"EST","latitude":""}],"customerConfirmationNumber":"","pickup":5352172,"trackingNumber":"420300419361269903505760403387"}]}
     */

    private MetaBean meta;
    private DataBean data;

    @Override
    public String toString() {
        return "EventResponse{" +
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
         * timestamp : 2020-11-10T00:52:59-05:00
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
         * status : {"numRejected":0,"numAccepted":1}
         * mailItems : [{"events":[{"location":"Atlanta, GA","zip":"","DHLGMEVENTID":110,"longitude":"","attemptCount":1,"state":"","eventId":"CE108110","time":"12:01:30","hubCode":"","eventDescription":"Doing pickup","delivery":{"secondaryEventDescription":"","secondaryEventId":"","originalAmount":"","signedByName":"","moneyTransactionType":"","remarks":"String Content","actualAmount":"","signedByImage":"","codCollected":""},"date":"11/11/2020","eventClass":"PICKUP","country":"US","container":"String Content","timezone":"EST","latitude":""}],"customerConfirmationNumber":"","pickup":5352172,"trackingNumber":"420300419361269903505760403387"}]
         */

        private StatusBean status;
        private List<MailItemsBean> mailItems;

        @Override
        public String toString() {
            return "DataBean{" +
                    "status=" + status +
                    ", mailItems=" + mailItems +
                    '}';
        }

        public StatusBean getStatus() {
            return status;
        }

        public void setStatus(StatusBean status) {
            this.status = status;
        }

        public List<MailItemsBean> getMailItems() {
            return mailItems;
        }

        public void setMailItems(List<MailItemsBean> mailItems) {
            this.mailItems = mailItems;
        }

        public static class StatusBean {
            /**
             * numRejected : 0
             * numAccepted : 1.0
             */

            private int numRejected;
            private double numAccepted;

            @Override
            public String toString() {
                return "StatusBean{" +
                        "numRejected=" + numRejected +
                        ", numAccepted=" + numAccepted +
                        '}';
            }

            public int getNumRejected() {
                return numRejected;
            }

            public void setNumRejected(int numRejected) {
                this.numRejected = numRejected;
            }

            public double getNumAccepted() {
                return numAccepted;
            }

            public void setNumAccepted(double numAccepted) {
                this.numAccepted = numAccepted;
            }
        }

        public static class MailItemsBean {
            /**
             * events : [{"location":"Atlanta, GA","zip":"","DHLGMEVENTID":110,"longitude":"","attemptCount":1,"state":"","eventId":"CE108110","time":"12:01:30","hubCode":"","eventDescription":"Doing pickup","delivery":{"secondaryEventDescription":"","secondaryEventId":"","originalAmount":"","signedByName":"","moneyTransactionType":"","remarks":"String Content","actualAmount":"","signedByImage":"","codCollected":""},"date":"11/11/2020","eventClass":"PICKUP","country":"US","container":"String Content","timezone":"EST","latitude":""}]
             * customerConfirmationNumber :
             * pickup : 5352172
             * trackingNumber : 420300419361269903505760403387
             */

            private String customerConfirmationNumber;
            private int pickup;
            private String trackingNumber;
            private List<EventsBean> events;

            @Override
            public String toString() {
                return "MailItemsBean{" +
                        "customerConfirmationNumber='" + customerConfirmationNumber + '\'' +
                        ", pickup=" + pickup +
                        ", trackingNumber='" + trackingNumber + '\'' +
                        ", events=" + events +
                        '}';
            }

            public String getCustomerConfirmationNumber() {
                return customerConfirmationNumber;
            }

            public void setCustomerConfirmationNumber(String customerConfirmationNumber) {
                this.customerConfirmationNumber = customerConfirmationNumber;
            }

            public int getPickup() {
                return pickup;
            }

            public void setPickup(int pickup) {
                this.pickup = pickup;
            }

            public String getTrackingNumber() {
                return trackingNumber;
            }

            public void setTrackingNumber(String trackingNumber) {
                this.trackingNumber = trackingNumber;
            }

            public List<EventsBean> getEvents() {
                return events;
            }

            public void setEvents(List<EventsBean> events) {
                this.events = events;
            }

            public static class EventsBean {
                /**
                 * location : Atlanta, GA
                 * zip :
                 * DHLGMEVENTID : 110
                 * longitude :
                 * attemptCount : 1
                 * state :
                 * eventId : CE108110
                 * time : 12:01:30
                 * hubCode :
                 * eventDescription : Doing pickup
                 * delivery : {"secondaryEventDescription":"","secondaryEventId":"","originalAmount":"","signedByName":"","moneyTransactionType":"","remarks":"String Content","actualAmount":"","signedByImage":"","codCollected":""}
                 * date : 11/11/2020
                 * eventClass : PICKUP
                 * country : US
                 * container : String Content
                 * timezone : EST
                 * latitude :
                 */

                private String location;
                private String zip;
                private int DHLGMEVENTID;
                private String longitude;
                private int attemptCount;
                private String state;
                private String eventId;
                private String time;
                private String hubCode;
                private String eventDescription;
                private DeliveryBean delivery;
                private String date;
                private String eventClass;
                private String country;
                private String container;
                private String timezone;
                private String latitude;

                @Override
                public String toString() {
                    return "EventsBean{" +
                            "location='" + location + '\'' +
                            ", zip='" + zip + '\'' +
                            ", DHLGMEVENTID=" + DHLGMEVENTID +
                            ", longitude='" + longitude + '\'' +
                            ", attemptCount=" + attemptCount +
                            ", state='" + state + '\'' +
                            ", eventId='" + eventId + '\'' +
                            ", time='" + time + '\'' +
                            ", hubCode='" + hubCode + '\'' +
                            ", eventDescription='" + eventDescription + '\'' +
                            ", delivery=" + delivery +
                            ", date='" + date + '\'' +
                            ", eventClass='" + eventClass + '\'' +
                            ", country='" + country + '\'' +
                            ", container='" + container + '\'' +
                            ", timezone='" + timezone + '\'' +
                            ", latitude='" + latitude + '\'' +
                            '}';
                }

                public String getLocation() {
                    return location;
                }

                public void setLocation(String location) {
                    this.location = location;
                }

                public String getZip() {
                    return zip;
                }

                public void setZip(String zip) {
                    this.zip = zip;
                }

                public int getDHLGMEVENTID() {
                    return DHLGMEVENTID;
                }

                public void setDHLGMEVENTID(int DHLGMEVENTID) {
                    this.DHLGMEVENTID = DHLGMEVENTID;
                }

                public String getLongitude() {
                    return longitude;
                }

                public void setLongitude(String longitude) {
                    this.longitude = longitude;
                }

                public int getAttemptCount() {
                    return attemptCount;
                }

                public void setAttemptCount(int attemptCount) {
                    this.attemptCount = attemptCount;
                }

                public String getState() {
                    return state;
                }

                public void setState(String state) {
                    this.state = state;
                }

                public String getEventId() {
                    return eventId;
                }

                public void setEventId(String eventId) {
                    this.eventId = eventId;
                }

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public String getHubCode() {
                    return hubCode;
                }

                public void setHubCode(String hubCode) {
                    this.hubCode = hubCode;
                }

                public String getEventDescription() {
                    return eventDescription;
                }

                public void setEventDescription(String eventDescription) {
                    this.eventDescription = eventDescription;
                }

                public DeliveryBean getDelivery() {
                    return delivery;
                }

                public void setDelivery(DeliveryBean delivery) {
                    this.delivery = delivery;
                }

                public String getDate() {
                    return date;
                }

                public void setDate(String date) {
                    this.date = date;
                }

                public String getEventClass() {
                    return eventClass;
                }

                public void setEventClass(String eventClass) {
                    this.eventClass = eventClass;
                }

                public String getCountry() {
                    return country;
                }

                public void setCountry(String country) {
                    this.country = country;
                }

                public String getContainer() {
                    return container;
                }

                public void setContainer(String container) {
                    this.container = container;
                }

                public String getTimezone() {
                    return timezone;
                }

                public void setTimezone(String timezone) {
                    this.timezone = timezone;
                }

                public String getLatitude() {
                    return latitude;
                }

                public void setLatitude(String latitude) {
                    this.latitude = latitude;
                }

                public static class DeliveryBean {
                    /**
                     * secondaryEventDescription :
                     * secondaryEventId :
                     * originalAmount :
                     * signedByName :
                     * moneyTransactionType :
                     * remarks : String Content
                     * actualAmount :
                     * signedByImage :
                     * codCollected :
                     */

                    private String secondaryEventDescription;
                    private String secondaryEventId;
                    private String originalAmount;
                    private String signedByName;
                    private String moneyTransactionType;
                    private String remarks;
                    private String actualAmount;
                    private String signedByImage;
                    private String codCollected;

                    @Override
                    public String toString() {
                        return "DeliveryBean{" +
                                "secondaryEventDescription='" + secondaryEventDescription + '\'' +
                                ", secondaryEventId='" + secondaryEventId + '\'' +
                                ", originalAmount='" + originalAmount + '\'' +
                                ", signedByName='" + signedByName + '\'' +
                                ", moneyTransactionType='" + moneyTransactionType + '\'' +
                                ", remarks='" + remarks + '\'' +
                                ", actualAmount='" + actualAmount + '\'' +
                                ", signedByImage='" + signedByImage + '\'' +
                                ", codCollected='" + codCollected + '\'' +
                                '}';
                    }

                    public String getSecondaryEventDescription() {
                        return secondaryEventDescription;
                    }

                    public void setSecondaryEventDescription(String secondaryEventDescription) {
                        this.secondaryEventDescription = secondaryEventDescription;
                    }

                    public String getSecondaryEventId() {
                        return secondaryEventId;
                    }

                    public void setSecondaryEventId(String secondaryEventId) {
                        this.secondaryEventId = secondaryEventId;
                    }

                    public String getOriginalAmount() {
                        return originalAmount;
                    }

                    public void setOriginalAmount(String originalAmount) {
                        this.originalAmount = originalAmount;
                    }

                    public String getSignedByName() {
                        return signedByName;
                    }

                    public void setSignedByName(String signedByName) {
                        this.signedByName = signedByName;
                    }

                    public String getMoneyTransactionType() {
                        return moneyTransactionType;
                    }

                    public void setMoneyTransactionType(String moneyTransactionType) {
                        this.moneyTransactionType = moneyTransactionType;
                    }

                    public String getRemarks() {
                        return remarks;
                    }

                    public void setRemarks(String remarks) {
                        this.remarks = remarks;
                    }

                    public String getActualAmount() {
                        return actualAmount;
                    }

                    public void setActualAmount(String actualAmount) {
                        this.actualAmount = actualAmount;
                    }

                    public String getSignedByImage() {
                        return signedByImage;
                    }

                    public void setSignedByImage(String signedByImage) {
                        this.signedByImage = signedByImage;
                    }

                    public String getCodCollected() {
                        return codCollected;
                    }

                    public void setCodCollected(String codCollected) {
                        this.codCollected = codCollected;
                    }
                }
            }
        }
    }
}
