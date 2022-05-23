package com.hgups.express.business.dhl.label;

import lombok.Data;

import java.util.List;

public class LabelParameter {

    private List<ShipmentsBean> shipments;

    public List<ShipmentsBean> getShipments() {
        return shipments;
    }

    public void setShipments(List<ShipmentsBean> shipments) {
        this.shipments = shipments;
    }

    public static class ShipmentsBean {
        /**
         * pickup : 5352172
         * distributionCenter : USSFO1
         * packages : [{"consigneeAddress":{"address1":"123 Main St","address2":"","city":"Cumming","country":"US","email":"test@email.com","name":"Test Name","phone":"555-555-5555","postalCode":"30041","state":"GA"},"packageDetails":{"billingRef1":"test ref 1","billingRef2":"test ref 2","codAmount":0,"currency":"USD","declaredValue":250,"dgCategory":"","dimensionUom":"IN","dutyCharges":10,"dutiesPaid":"N","freightCharges":10,"insuredValue":1,"mailtype":"7","orderedProduct":"83","packageDesc":"Desc","packageId":"888888888888888888888882","packageRefName":"NZCINTL1000194","taxCharges":10,"weight":1,"weightUom":"LB"},"returnAddress":{"address1":"Address line 1","city":"Test City","companyName":"Test Company","country":"US","name":"John Returns Doe","postalCode":"99999","state":"GA"},"customsDetails":[{"itemDescription":"Desc","countryOfOrigin":"US","hsCode":"888888888888","packagedQuantity":10,"itemValue":10,"skuNumber":"3333333333333"}]}]
         */

        private String pickup;
        private String distributionCenter;
        private List<PackagesBean> packages;

        public String getPickup() {
            return pickup;
        }

        public void setPickup(String pickup) {
            this.pickup = pickup;
        }

        public String getDistributionCenter() {
            return distributionCenter;
        }

        public void setDistributionCenter(String distributionCenter) {
            this.distributionCenter = distributionCenter;
        }

        public List<PackagesBean> getPackages() {
            return packages;
        }

        public void setPackages(List<PackagesBean> packages) {
            this.packages = packages;
        }

        @Override
        public String toString() {
            return "ShipmentsBean{" +
                    "pickup='" + pickup + '\'' +
                    ", distributionCenter='" + distributionCenter + '\'' +
                    ", packages=" + packages +
                    '}';
        }

        public static class PackagesBean {
            /**
             * consigneeAddress : {"address1":"123 Main St","address2":"","city":"Cumming","country":"US","email":"test@email.com","name":"Test Name","phone":"555-555-5555","postalCode":"30041","state":"GA"}
             * packageDetails : {"billingRef1":"test ref 1","billingRef2":"test ref 2","codAmount":0,"currency":"USD","declaredValue":250,"dgCategory":"","dimensionUom":"IN","dutyCharges":10,"dutiesPaid":"N","freightCharges":10,"insuredValue":1,"mailtype":"7","orderedProduct":"83","packageDesc":"Desc","packageId":"888888888888888888888882","packageRefName":"NZCINTL1000194","taxCharges":10,"weight":1,"weightUom":"LB"}
             * returnAddress : {"address1":"Address line 1","city":"Test City","companyName":"Test Company","country":"US","name":"John Returns Doe","postalCode":"99999","state":"GA"}
             * customsDetails : [{"itemDescription":"Desc","countryOfOrigin":"US","hsCode":"888888888888","packagedQuantity":10,"itemValue":10,"skuNumber":"3333333333333"}]
             */

            private ConsigneeAddressBean consigneeAddress;
            private PackageDetailsBean packageDetails;
            private ReturnAddressBean returnAddress;

            @Override
            public String toString() {
                return "PackagesBean{" +
                        "consigneeAddress=" + consigneeAddress +
                        ", packageDetails=" + packageDetails +
                        ", returnAddress=" + returnAddress +
                        '}';
            }

            public ConsigneeAddressBean getConsigneeAddress() {
                return consigneeAddress;
            }


            public void setConsigneeAddress(ConsigneeAddressBean consigneeAddress) {
                this.consigneeAddress = consigneeAddress;
            }

            public PackageDetailsBean getPackageDetails() {
                return packageDetails;
            }

            public void setPackageDetails(PackageDetailsBean packageDetails) {
                this.packageDetails = packageDetails;
            }

            public ReturnAddressBean getReturnAddress() {
                return returnAddress;
            }

            public void setReturnAddress(ReturnAddressBean returnAddress) {
                this.returnAddress = returnAddress;
            }

            public static class ConsigneeAddressBean {
                /**
                 * address1 : 123 Main St
                 * address2 :
                 * city : Cumming
                 * country : US
                 * email : test@email.com
                 * name : Test Name
                 * phone : 555-555-5555
                 * postalCode : 30041
                 * state : GA
                 */

                public String address1 ;
                private String address2 = "";
                private String city;
                private String country;
                private String email;
                private String name;
                private String companyName;
                private String phone;
                private String postalCode;
                private String state;

                @Override
                public String toString() {
                    return "ConsigneeAddressBean{" +
                            "address1='" + address1 + '\'' +
                            ", address2='" + address2 + '\'' +
                            ", city='" + city + '\'' +
                            ", country='" + country + '\'' +
                            ", email='" + email + '\'' +
                            ", name='" + name + '\'' +
                            ", companyName='" + companyName + '\'' +
                            ", phone='" + phone + '\'' +
                            ", postalCode='" + postalCode + '\'' +
                            ", state='" + state + '\'' +
                            '}';
                }

                public String getCompanyName() {
                    return companyName;
                }

                public void setCompanyName(String companyName) {
                    this.companyName = companyName;
                }

                public String getAddress1() {
                    return address1;
                }

                public void setAddress1(String address1) {
                    this.address1 = address1;
                }

                public String getAddress2() {
                    return address2;
                }

                public void setAddress2(String address2) {
                    this.address2 = address2;
                }

                public String getCity() {
                    return city;
                }

                public void setCity(String city) {
                    this.city = city;
                }

                public String getCountry() {
                    return country;
                }

                public void setCountry(String country) {
                    this.country = country;
                }

                public String getEmail() {
                    return email;
                }

                public void setEmail(String email) {
                    this.email = email;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPhone() {
                    return phone;
                }

                public void setPhone(String phone) {
                    this.phone = phone;
                }

                public String getPostalCode() {
                    return postalCode;
                }

                public void setPostalCode(String postalCode) {
                    this.postalCode = postalCode;
                }

                public String getState() {
                    return state;
                }

                public void setState(String state) {
                    this.state = state;
                }
            }

            @Data
            public static class PackageDetailsBean {
                /**
                 * billingRef1 : test ref 1
                 * billingRef2 : test ref 2
                 * codAmount : 0
                 * currency : USD
                 * declaredValue : 250.0
                 * dgCategory :
                 * dimensionUom : IN
                 * dutyCharges : 10.0
                 * dutiesPaid : N
                 * freightCharges : 10.0
                 * insuredValue : 1
                 * mailtype : 7
                 * orderedProduct : 83
                 * packageDesc : Desc
                 * packageId : 888888888888888888888882
                 * packageRefName : NZCINTL1000194
                 * taxCharges : 10.0
                 * weight : 1
                 * weightUom : LB
                 */

                private String billingRef1;
                private String billingRef2;
                private int codAmount;
                private String currency = "USD";
                private double declaredValue;
                private String dgCategory;
                private String dimensionUom;
                private double dutyCharges;
                private String dutiesPaid;
                private double freightCharges;
                private int insuredValue;
                private String mailType;
                private String orderedProduct;
                private String packageDesc;
                private String packageId;
                private String packageRefName;
                private double taxCharges;
                private double weight;
                private String weightUom;
                private int length;
                private int width;
                private int height;

                @Override
                public String toString() {
                    return "PackageDetailsBean{" +
                            "billingRef1='" + billingRef1 + '\'' +
                            ", billingRef2='" + billingRef2 + '\'' +
                            ", codAmount=" + codAmount +
                            ", currency='" + currency + '\'' +
                            ", declaredValue=" + declaredValue +
                            ", dgCategory='" + dgCategory + '\'' +
                            ", dimensionUom='" + dimensionUom + '\'' +
                            ", dutyCharges=" + dutyCharges +
                            ", dutiesPaid='" + dutiesPaid + '\'' +
                            ", freightCharges=" + freightCharges +
                            ", insuredValue=" + insuredValue +
                            ", mailType='" + mailType + '\'' +
                            ", orderedProduct='" + orderedProduct + '\'' +
                            ", packageDesc='" + packageDesc + '\'' +
                            ", packageId='" + packageId + '\'' +
                            ", packageRefName='" + packageRefName + '\'' +
                            ", taxCharges=" + taxCharges +
                            ", weight=" + weight +
                            ", weightUom='" + weightUom + '\'' +
                            '}';
                }

                public String getBillingRef1() {
                    return billingRef1;
                }

                public void setBillingRef1(String billingRef1) {
                    this.billingRef1 = billingRef1;
                }

                public String getBillingRef2() {
                    return billingRef2;
                }

                public void setBillingRef2(String billingRef2) {
                    this.billingRef2 = billingRef2;
                }

                public int getCodAmount() {
                    return codAmount;
                }

                public void setCodAmount(int codAmount) {
                    this.codAmount = codAmount;
                }

                public String getCurrency() {
                    return currency;
                }

                public void setCurrency(String currency) {
                    this.currency = currency;
                }

                public double getDeclaredValue() {
                    return declaredValue;
                }

                public void setDeclaredValue(double declaredValue) {
                    this.declaredValue = declaredValue;
                }

                public String getDgCategory() {
                    return dgCategory;
                }

                public void setDgCategory(String dgCategory) {
                    this.dgCategory = dgCategory;
                }

                public String getDimensionUom() {
                    return dimensionUom;
                }

                public void setDimensionUom(String dimensionUom) {
                    this.dimensionUom = dimensionUom;
                }

                public double getDutyCharges() {
                    return dutyCharges;
                }

                public void setDutyCharges(double dutyCharges) {
                    this.dutyCharges = dutyCharges;
                }

                public String getDutiesPaid() {
                    return dutiesPaid;
                }

                public void setDutiesPaid(String dutiesPaid) {
                    this.dutiesPaid = dutiesPaid;
                }

                public double getFreightCharges() {
                    return freightCharges;
                }

                public void setFreightCharges(double freightCharges) {
                    this.freightCharges = freightCharges;
                }

                public int getInsuredValue() {
                    return insuredValue;
                }

                public void setInsuredValue(int insuredValue) {
                    this.insuredValue = insuredValue;
                }

                public String getMailtype() {
                    return mailType;
                }

                public void setMailtype(String mailtype) {
                    this.mailType = mailtype;
                }

                /**
                 * 设置是P单还是F单
                 * @param isF
                 */
                public void setMailFOrP(boolean isF) {
                    setMailtype(isF ? "7" : "3");
                    setOrderedProduct(isF ? "83" : "81");
                }

                public String getOrderedProduct() {
                    return orderedProduct;
                }

                public void setOrderedProduct(String orderedProduct) {
                    this.orderedProduct = orderedProduct;
                }

                public String getPackageDesc() {
                    return packageDesc;
                }

                public void setPackageDesc(String packageDesc) {
                    this.packageDesc = packageDesc;
                }

                public String getPackageId() {
                    return packageId;
                }

                public void setPackageId(String packageId) {
                    this.packageId = packageId;
                }

                public String getPackageRefName() {
                    return packageRefName;
                }

                public void setPackageRefName(String packageRefName) {
                    this.packageRefName = packageRefName;
                }

                public double getTaxCharges() {
                    return taxCharges;
                }

                public void setTaxCharges(double taxCharges) {
                    this.taxCharges = taxCharges;
                }

                public double getWeight() {
                    return weight;
                }

                public void setWeight(double weight) {
                    this.weight = weight;
                }

                public String getWeightUom() {
                    return weightUom;
                }

                public void setWeightUom(String weightUom) {
                    this.weightUom = weightUom;
                }
            }

            /**
             * 在HGUPS系统中收集到的信息，用发件人地址来代替
             */
            public static class ReturnAddressBean {
                /**
                 * address1 : Address line 1
                 * city : Test City
                 * companyName : Test Company
                 * country : US
                 * name : John Returns Doe
                 * postalCode : 99999
                 * state : GA
                 */

                private String address1;
                private String city;
                private String companyName;
                private String country;
                private String name;
                private String postalCode;
                private String state;

                @Override
                public String toString() {
                    return "ReturnAddressBean{" +
                            "address1='" + address1 + '\'' +
                            ", city='" + city + '\'' +
                            ", companyName='" + companyName + '\'' +
                            ", country='" + country + '\'' +
                            ", name='" + name + '\'' +
                            ", postalCode='" + postalCode + '\'' +
                            ", state='" + state + '\'' +
                            '}';
                }

                public String getAddress1() {
                    return address1;
                }

                public void setAddress1(String address1) {
                    this.address1 = address1;
                }

                public String getCity() {
                    return city;
                }

                public void setCity(String city) {
                    this.city = city;
                }

                public String getCompanyName() {
                    return companyName;
                }

                public void setCompanyName(String companyName) {
                    this.companyName = companyName;
                }

                public String getCountry() {
                    return country;
                }

                public void setCountry(String country) {
                    this.country = country;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getPostalCode() {
                    return postalCode;
                }

                public void setPostalCode(String postalCode) {
                    this.postalCode = postalCode;
                }

                public String getState() {
                    return state;
                }

                public void setState(String state) {
                    this.state = state;
                }
            }

        }
    }

    @Override
    public String toString() {
        return "LabelParameter{" +
                "shipments=" + shipments +
                '}';
    }
}
