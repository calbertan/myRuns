package com.example.myRuns;

class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N67028d300(i);
        return p;
    }
    static double N67028d300(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 110.672015) {
            p = WekaClassifier.N7e1d53091(i);
        } else if (((Double) i[0]).doubleValue() > 110.672015) {
            p = WekaClassifier.N3d1b49ea4(i);
        }
        return p;
    }
    static double N7e1d53091(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 86.387993) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 86.387993) {
            p = WekaClassifier.N3f2d3aca2(i);
        }
        return p;
    }
    static double N3f2d3aca2(Object []i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 25.177673) {
            p = WekaClassifier.N4e8326073(i);
        } else if (((Double) i[1]).doubleValue() > 25.177673) {
            p = 0;
        }
        return p;
    }
    static double N4e8326073(Object []i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() <= 9.353713) {
            p = 0;
        } else if (((Double) i[4]).doubleValue() > 9.353713) {
            p = 1;
        }
        return p;
    }
    static double N3d1b49ea4(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 1;
        } else if (((Double) i[64]).doubleValue() <= 24.396242) {
            p = WekaClassifier.N298ac05b5(i);
        } else if (((Double) i[64]).doubleValue() > 24.396242) {
            p = 2;
        }
        return p;
    }
    static double N298ac05b5(Object []i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 69.56357) {
            p = WekaClassifier.N7f2744ca6(i);
        } else if (((Double) i[3]).doubleValue() > 69.56357) {
            p = WekaClassifier.N1fb2200115(i);
        }
        return p;
    }
    static double N7f2744ca6(Object []i) {
        double p = Double.NaN;
        if (i[11] == null) {
            p = 1;
        } else if (((Double) i[11]).doubleValue() <= 8.464121) {
            p = WekaClassifier.N28c3f6377(i);
        } else if (((Double) i[11]).doubleValue() > 8.464121) {
            p = WekaClassifier.N1ff44fb13(i);
        }
        return p;
    }
    static double N28c3f6377(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 281.653327) {
            p = WekaClassifier.N4ff0aedc8(i);
        } else if (((Double) i[0]).doubleValue() > 281.653327) {
            p = 1;
        }
        return p;
    }
    static double N4ff0aedc8(Object []i) {
        double p = Double.NaN;
        if (i[19] == null) {
            p = 1;
        } else if (((Double) i[19]).doubleValue() <= 0.952152) {
            p = 1;
        } else if (((Double) i[19]).doubleValue() > 0.952152) {
            p = WekaClassifier.N308727619(i);
        }
        return p;
    }
    static double N308727619(Object []i) {
        double p = Double.NaN;
        if (i[20] == null) {
            p = 0;
        } else if (((Double) i[20]).doubleValue() <= 0.760457) {
            p = 0;
        } else if (((Double) i[20]).doubleValue() > 0.760457) {
            p = WekaClassifier.N754334af10(i);
        }
        return p;
    }
    static double N754334af10(Object []i) {
        double p = Double.NaN;
        if (i[29] == null) {
            p = 1;
        } else if (((Double) i[29]).doubleValue() <= 0.882115) {
            p = 1;
        } else if (((Double) i[29]).doubleValue() > 0.882115) {
            p = WekaClassifier.N4286167e11(i);
        }
        return p;
    }
    static double N4286167e11(Object []i) {
        double p = Double.NaN;
        if (i[64] == null) {
            p = 0;
        } else if (((Double) i[64]).doubleValue() <= 6.302075) {
            p = WekaClassifier.N84ad6bc12(i);
        } else if (((Double) i[64]).doubleValue() > 6.302075) {
            p = 1;
        }
        return p;
    }
    static double N84ad6bc12(Object []i) {
        double p = Double.NaN;
        if (i[27] == null) {
            p = 1;
        } else if (((Double) i[27]).doubleValue() <= 0.896948) {
            p = 1;
        } else if (((Double) i[27]).doubleValue() > 0.896948) {
            p = 0;
        }
        return p;
    }
    static double N1ff44fb13(Object []i) {
        double p = Double.NaN;
        if (i[32] == null) {
            p = 0;
        } else if (((Double) i[32]).doubleValue() <= 2.523797) {
            p = 0;
        } else if (((Double) i[32]).doubleValue() > 2.523797) {
            p = WekaClassifier.N6df5ee2714(i);
        }
        return p;
    }
    static double N6df5ee2714(Object []i) {
        double p = Double.NaN;
        if (i[26] == null) {
            p = 2;
        } else if (((Double) i[26]).doubleValue() <= 4.645085) {
            p = 2;
        } else if (((Double) i[26]).doubleValue() > 4.645085) {
            p = 1;
        }
        return p;
    }
    static double N1fb2200115(Object []i) {
        double p = Double.NaN;
        if (i[10] == null) {
            p = 1;
        } else if (((Double) i[10]).doubleValue() <= 15.787186) {
            p = WekaClassifier.N4113fa7d16(i);
        } else if (((Double) i[10]).doubleValue() > 15.787186) {
            p = 2;
        }
        return p;
    }
    static double N4113fa7d16(Object []i) {
        double p = Double.NaN;
        if (i[26] == null) {
            p = 2;
        } else if (((Double) i[26]).doubleValue() <= 3.686452) {
            p = 2;
        } else if (((Double) i[26]).doubleValue() > 3.686452) {
            p = 1;
        }
        return p;
    }
}
