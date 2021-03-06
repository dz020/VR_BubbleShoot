package com.example.jackson.gameuebung_3.math;

/**
 * Created by Jackson on 04.04.2016.
 */
public class Vector4 {

    public float[] v = new float[4];

    /* Konstruktoren */
        public Vector4() {
            v[0] = 0.0f;
            v[1] = 0.0f;
            v[2] = 0.0f;
            v[3] = 0.0f;
        }

        public Vector4(Vector3 v3, float w) {
            v[0] = v3.v[0];
            v[1] = v3.v[1];
            v[2] = v3.v[2];
            v[3] = w;
        }

        public Vector4(Vector2 v2, float z, float w) {
            v[0] = v2.v[0];
            v[1] = v2.v[1];
            v[2] = z;
            v[3] = w;
        }

        public Vector4(float x, float y, float z, float w) {
            v[0] = x;
            v[1] = y;
            v[2] = z;
            v[3] = w;
        }

        /* Klassen Methoden */

                        /* getter und setter */
                        public float get(int index) {
                            return v[index];
                        }

                        public float getX() {
                            return v[0];
                        }

                        public float getY() {
                            return v[1];
                        }

                        public float getZ() {
                            return v[2];
                        }

                        public float getW() {
                            return v[3];
                        }

                        public void set(int index, float value) {
                            v[index] = value;
                        }

                        public void setX(float x) {
                            v[0] = x;
                        }

                        public void setY(float y) {
                            v[1] = y;
                        }

                        public void setZ(float z) {
                            v[2] = z;
                        }

                        public void setW(float w) {
                            v[3] = w;
                        }



                /**
                 * Die Länge eines Vektors
                 * @return
                 */
                public float getLength() {
                    return (float) Math.sqrt(
                            v[0] * v[0] +
                                    v[1] * v[1] +
                                    v[2] * v[2] +
                                    v[3] * v[3]);
                }

                /**
                 *
                 * @return
                 */
                public float getLengthSqr() {                                                           //für was ist die methode ??
                    return v[0] * v[0] +
                            v[1] * v[1] +
                            v[2] * v[2] +
                            v[3] * v[3];
                }

                /**
                 * Vektor auf Länge 1 bringen, dann ist er ein Einheitsvektor
                 * @return
                 */
                public Vector4 normalize() {
                    float l = getLength();
                    for (int i = 0; i < 4; ++i)
                        v[i] /= l;
                    return this;
                }



    /* statische Methoden */

        public static Vector4 add(Vector4 v1, Vector4 v2) {
            return new Vector4(
                    v1.v[0] + v2.v[0],
                    v1.v[1] + v2.v[1],
                    v1.v[2] + v2.v[2],
                    v1.v[3] + v2.v[3]);
        }

        public static Vector4 subtract(Vector4 v1, Vector4 v2) {
            return new Vector4(
                    v1.v[0] - v2.v[0],
                    v1.v[1] - v2.v[1],
                    v1.v[2] - v2.v[2],
                    v1.v[3] - v2.v[3]);
        }

        public static Vector4 multiply(float s, Vector4 v) { //für was steht s ??
            return new Vector4(
                    s * v.v[0],
                    s * v.v[1],
                    s * v.v[2],
                    s * v.v[3]);
        }

        public static Vector4 divide(Vector4 v, float s) { //für was steht s ??
            return new Vector4(
                    v.v[0] / s,
                    v.v[1] / s,
                    v.v[2] / s,
                    v.v[3] / s);
        }

        /**
         * Vektor auf Länge 1 bringen, dann ist er ein Einheitsvektor
         * @return
         */
        public static Vector4 normalize(Vector4 v) { //für was ist die methode ??
            Vector4 result = new Vector4();
            float l = v.getLength();
            for (int i = 0; i < 4; ++i)
                result.v[i] /= l;
            return result;
        }

        /**
         * Skalarprodukt, wenn return value = 0, dann sind Vektoren orthogonal zueinander, also im rechten Winkel
         * @param v1
         * @param v2
         * @return
         */
        public static float scalar(Vector4 v1, Vector4 v2) { //für was ist die methode ??
            return v1.v[0] * v2.v[0] + v1.v[1] * v2.v[1] + v1.v[2] * v2.v[2] + v1.v[3] * v2.v[3];
        }
}
