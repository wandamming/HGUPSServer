package com.hgups.express.business.dhl.track;

import java.util.List;

public class EventParameter {

    public List<MailItem> mailitems;

    @Override
    public String toString() {
        return "EventParameter{" +
                "mailitems=" + mailitems +
                '}';
    }

    public static class MailItem {

        /**
         * trackingNumber : 9361269903505270998014
         * pickup : 5352173
         * customerConfirmationNumber :
         */

        private String trackingNumber;
        private String pickup;
        private String customerConfirmationNumber;
        private List<Event> events;
        private Object packageData = new Object();

        public Object getPackageData() {
            return packageData;
        }

        public void setPackageData(Object packageData) {
            this.packageData = packageData;
        }

        public List<Event> getEvents() {
            return events;
        }

        public void setEvents(List<Event> events) {
            this.events = events;
        }

        @Override
        public String toString() {
            return "MailItem{" +
                    "trackingNumber='" + trackingNumber + '\'' +
                    ", pickup='" + pickup + '\'' +
                    ", customerConfirmationNumber='" + customerConfirmationNumber + '\'' +
                    ", events=" + events +
                    ", packageData=" + packageData +
                    '}';
        }

        public String getTrackingNumber() {
            return trackingNumber;
        }

        public void setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }

        public String getPickup() {
            return pickup;
        }

        public void setPickup(String pickup) {
            this.pickup = pickup;
        }

        public String getCustomerConfirmationNumber() {
            return customerConfirmationNumber;
        }

        public void setCustomerConfirmationNumber(String customerConfirmationNumber) {
            this.customerConfirmationNumber = customerConfirmationNumber;
        }


        public static class Event {

            /**
             * eventClass : PICKUP
             * eventId : CE108110
             * eventDescription :
             * location : Atlanta, GA
             * state :
             * zip :
             * country : US
             * date : 07/29/2020
             * time : 12:01:30
             * timezone : EST
             * latitude :
             * longitude :
             * hubCode :
             * container : String Content
             * attemptCount : 1
             */

            private String eventClass;
            private String eventId;
            private String eventDescription;
            private String location;
            private String state;
            private String zip;
            private String country;
            private String date;
            private String time;
            private String timezone;
            private String latitude;
            private String longitude;
            private String hubCode;
            private String container;
            private String attemptCount;
            private Delivery delivery;

            @Override
            public String toString() {
                return "Event{" +
                        "eventClass='" + eventClass + '\'' +
                        ", eventId='" + eventId + '\'' +
                        ", eventDescription='" + eventDescription + '\'' +
                        ", location='" + location + '\'' +
                        ", state='" + state + '\'' +
                        ", zip='" + zip + '\'' +
                        ", country='" + country + '\'' +
                        ", date='" + date + '\'' +
                        ", time='" + time + '\'' +
                        ", timezone='" + timezone + '\'' +
                        ", latitude='" + latitude + '\'' +
                        ", longitude='" + longitude + '\'' +
                        ", hubCode='" + hubCode + '\'' +
                        ", container='" + container + '\'' +
                        ", attemptCount='" + attemptCount + '\'' +
                        ", delivery=" + delivery +
                        '}';
            }

            public Delivery getDelivery() {
                return delivery;
            }

            public void setDelivery(Delivery delivery) {
                this.delivery = delivery;
            }

            public String getEventClass() {
                return eventClass;
            }

            public void setEventClass(String eventClass) {
                this.eventClass = eventClass;
            }

            public String getEventId() {
                return eventId;
            }

            public void setEventId(String eventId) {
                this.eventId = eventId;
            }

            public String getEventDescription() {
                return eventDescription;
            }

            public void setEventDescription(String eventDescription) {
                this.eventDescription = eventDescription;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getZip() {
                return zip;
            }

            public void setZip(String zip) {
                this.zip = zip;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
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

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            public String getHubCode() {
                return hubCode;
            }

            public void setHubCode(String hubCode) {
                this.hubCode = hubCode;
            }

            public String getContainer() {
                return container;
            }

            public void setContainer(String container) {
                this.container = container;
            }

            public String getAttemptCount() {
                return attemptCount;
            }

            public void setAttemptCount(String attemptCount) {
                this.attemptCount = attemptCount;
            }


            public static class Delivery {


                /**
                 * signedByName :
                 * signedByImage :
                 * secondaryEventId :
                 * secondaryEventDescription :
                 * remarks : String Content
                 * codCollected :
                 * actualAmount :
                 * originalAmount :
                 * moneyTransactionType :
                 */

                private String signedByName;
                private String signedByImage;
                private String secondaryEventId;
                private String secondaryEventDescription;
                private String remarks;
                private String codCollected;
                private String actualAmount;
                private String originalAmount;
                private String moneyTransactionType;

                @Override
                public String toString() {
                    return "Delivery{" +
                            "signedByName='" + signedByName + '\'' +
                            ", signedByImage='" + signedByImage + '\'' +
                            ", secondaryEventId='" + secondaryEventId + '\'' +
                            ", secondaryEventDescription='" + secondaryEventDescription + '\'' +
                            ", remarks='" + remarks + '\'' +
                            ", codCollected='" + codCollected + '\'' +
                            ", actualAmount='" + actualAmount + '\'' +
                            ", originalAmount='" + originalAmount + '\'' +
                            ", moneyTransactionType='" + moneyTransactionType + '\'' +
                            '}';
                }

                public String getSignedByName() {
                    return signedByName;
                }

                public void setSignedByName(String signedByName) {
                    this.signedByName = signedByName;
                }

                public String getSignedByImage() {
                    return signedByImage;
                }

                public void setSignedByImage(String signedByImage) {
                    this.signedByImage = signedByImage;
                }

                public String getSecondaryEventId() {
                    return secondaryEventId;
                }

                public void setSecondaryEventId(String secondaryEventId) {
                    this.secondaryEventId = secondaryEventId;
                }

                public String getSecondaryEventDescription() {
                    return secondaryEventDescription;
                }

                public void setSecondaryEventDescription(String secondaryEventDescription) {
                    this.secondaryEventDescription = secondaryEventDescription;
                }

                public String getRemarks() {
                    return remarks;
                }

                public void setRemarks(String remarks) {
                    this.remarks = remarks;
                }

                public String getCodCollected() {
                    return codCollected;
                }

                public void setCodCollected(String codCollected) {
                    this.codCollected = codCollected;
                }

                public String getActualAmount() {
                    return actualAmount;
                }

                public void setActualAmount(String actualAmount) {
                    this.actualAmount = actualAmount;
                }

                public String getOriginalAmount() {
                    return originalAmount;
                }

                public void setOriginalAmount(String originalAmount) {
                    this.originalAmount = originalAmount;
                }

                public String getMoneyTransactionType() {
                    return moneyTransactionType;
                }

                public void setMoneyTransactionType(String moneyTransactionType) {
                    this.moneyTransactionType = moneyTransactionType;
                }

            }
        }
    }
}
