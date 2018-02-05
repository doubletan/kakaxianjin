package com.xinhe.kakaxianjin.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 2017/7/27.
 */

public class CardBean implements Serializable {


    private List<OutputsBean> outputs;

    public List<OutputsBean> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<OutputsBean> outputs) {
        this.outputs = outputs;
    }

    @Override
    public String toString() {
        return "CardBean{" +
                "outputs=" + outputs +
                '}';
    }

    public static class OutputsBean implements Serializable {
        @Override
        public String toString() {
            return "OutputsBean{" +
                    "outputLabel='" + outputLabel + '\'' +
                    ", outputMulti=" + outputMulti +
                    ", outputValue=" + outputValue +
                    '}';
        }

        /**
         * outputLabel : ocr_id
         * outputMulti : {}
         * outputValue : {"dataType":50,"dataValue":{"address":"西安市周至县终南镇豆村北一街131号","birth":"19921228","config_str":{"side":"face"},"face_rect":{"angle":-84.94274139404297,"center":{"x":462.06103515625,"y":233.46119689941406},"size":{"height":113.44154357910156,"width":103.48052978515625}},"name":"尚朋","nationality":"汉","num":"61012419921228393X","request_id":"20170727013627_a8854af4613d20888453c4f0cb906b0f","sex":"男","success":true}}
         */

        private String outputLabel;
        private OutputMultiBean outputMulti;
        private OutputValueBean outputValue;

        public String getOutputLabel() {
            return outputLabel;
        }

        public void setOutputLabel(String outputLabel) {
            this.outputLabel = outputLabel;
        }

        public OutputMultiBean getOutputMulti() {
            return outputMulti;
        }

        public void setOutputMulti(OutputMultiBean outputMulti) {
            this.outputMulti = outputMulti;
        }

        public OutputValueBean getOutputValue() {
            return outputValue;
        }

        public void setOutputValue(OutputValueBean outputValue) {
            this.outputValue = outputValue;
        }

        public static class OutputMultiBean implements Serializable {
        }

        public static class OutputValueBean implements Serializable {
            @Override
            public String toString() {
                return "OutputValueBean{" +
                        "dataType=" + dataType +
                        ", dataValue=" + dataValue +
                        '}';
            }

            /**
             * dataType : 50
             * dataValue : {"address":"西安市周至县终南镇豆村北一街131号","birth":"19921228","config_str":{"side":"face"},"face_rect":{"angle":-84.94274139404297,"center":{"x":462.06103515625,"y":233.46119689941406},"size":{"height":113.44154357910156,"width":103.48052978515625}},"name":"尚朋","nationality":"汉","num":"61012419921228393X","request_id":"20170727013627_a8854af4613d20888453c4f0cb906b0f","sex":"男","success":true}
             */

            private int dataType;
            private DataValueBean dataValue;

            public int getDataType() {
                return dataType;
            }

            public void setDataType(int dataType) {
                this.dataType = dataType;
            }

            public DataValueBean getDataValue() {
                return dataValue;
            }

            public void setDataValue(DataValueBean dataValue) {
                this.dataValue = dataValue;
            }

            public static class DataValueBean implements Serializable {
                @Override
                public String toString() {
                    return "DataValueBean{" +
                            "address='" + address + '\'' +
                            ", birth='" + birth + '\'' +
                            ", config_str=" + config_str +
                            ", face_rect=" + face_rect +
                            ", name='" + name + '\'' +
                            ", nationality='" + nationality + '\'' +
                            ", num='" + num + '\'' +
                            ", request_id='" + request_id + '\'' +
                            ", sex='" + sex + '\'' +
                            ", success=" + success +
                            '}';
                }

                /**

                 */

                private String address;
                private String birth;
                private ConfigStrBean config_str;
                private FaceRectBean face_rect;
                private String name;
                private String nationality;
                private String num;
                private String request_id;
                private String sex;
                private boolean success;

                public String getAddress() {
                    return address;
                }

                public void setAddress(String address) {
                    this.address = address;
                }

                public String getBirth() {
                    return birth;
                }

                public void setBirth(String birth) {
                    this.birth = birth;
                }

                public ConfigStrBean getConfig_str() {
                    return config_str;
                }

                public void setConfig_str(ConfigStrBean config_str) {
                    this.config_str = config_str;
                }

                public FaceRectBean getFace_rect() {
                    return face_rect;
                }

                public void setFace_rect(FaceRectBean face_rect) {
                    this.face_rect = face_rect;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getNationality() {
                    return nationality;
                }

                public void setNationality(String nationality) {
                    this.nationality = nationality;
                }

                public String getNum() {
                    return num;
                }

                public void setNum(String num) {
                    this.num = num;
                }

                public String getRequest_id() {
                    return request_id;
                }

                public void setRequest_id(String request_id) {
                    this.request_id = request_id;
                }

                public String getSex() {
                    return sex;
                }

                public void setSex(String sex) {
                    this.sex = sex;
                }

                public boolean isSuccess() {
                    return success;
                }

                public void setSuccess(boolean success) {
                    this.success = success;
                }

                public static class ConfigStrBean implements Serializable {
                    @Override
                    public String toString() {
                        return "ConfigStrBean{" +
                                "side='" + side + '\'' +
                                '}';
                    }

                    /**
                     * side : face
                     */

                    private String side;

                    public String getSide() {
                        return side;
                    }

                    public void setSide(String side) {
                        this.side = side;
                    }
                }

                public static class FaceRectBean implements Serializable {
                    @Override
                    public String toString() {
                        return "FaceRectBean{" +
                                "angle=" + angle +
                                ", center=" + center +
                                ", size=" + size +
                                '}';
                    }

                    /**
                     * angle : -84.94274139404297
                     * center : {"x":462.06103515625,"y":233.46119689941406}
                     * size : {"height":113.44154357910156,"width":103.48052978515625}
                     */

                    private double angle;
                    private CenterBean center;
                    private SizeBean size;

                    public double getAngle() {
                        return angle;
                    }

                    public void setAngle(double angle) {
                        this.angle = angle;
                    }

                    public CenterBean getCenter() {
                        return center;
                    }

                    public void setCenter(CenterBean center) {
                        this.center = center;
                    }

                    public SizeBean getSize() {
                        return size;
                    }

                    public void setSize(SizeBean size) {
                        this.size = size;
                    }

                    public static class CenterBean implements Serializable {
                        @Override
                        public String toString() {
                            return "CenterBean{" +
                                    "x=" + x +
                                    ", y=" + y +
                                    '}';
                        }

                        /**
                         * x : 462.06103515625
                         * y : 233.46119689941406
                         */

                        private double x;
                        private double y;

                        public double getX() {
                            return x;
                        }

                        public void setX(double x) {
                            this.x = x;
                        }

                        public double getY() {
                            return y;
                        }

                        public void setY(double y) {
                            this.y = y;
                        }
                    }

                    public static class SizeBean implements Serializable {
                        @Override
                        public String toString() {
                            return "SizeBean{" +
                                    "height=" + height +
                                    ", width=" + width +
                                    '}';
                        }

                        /**
                         * height : 113.44154357910156
                         * width : 103.48052978515625
                         */

                        private double height;
                        private double width;

                        public double getHeight() {
                            return height;
                        }

                        public void setHeight(double height) {
                            this.height = height;
                        }

                        public double getWidth() {
                            return width;
                        }

                        public void setWidth(double width) {
                            this.width = width;
                        }
                    }
                }
            }
        }
    }
}
