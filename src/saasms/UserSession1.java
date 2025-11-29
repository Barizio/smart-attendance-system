/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package saasms;

/**
 *
 * @author adisa
 */
public class UserSession1 {

    private static String username;
    private static int staffID;
    private static String staffName;
    private static String staffUnit;
    private static String staffRole;

    public static void setUsername(String user) {
        username = user;
    }

    public static void setStaffID(int id) {
        staffID = id;
    }

    public static void setStaffName(String name) {
        staffName = name;
    }

    public static void setStaffUnit(String unit) {
        staffUnit = unit;
    }

    public static void setStaffRole(String role) {
        staffRole = role;
    }

    public static String getUsername() {
        return username;
    }

    public static int getStaffID() {
        return staffID;
    }

    public static String getStaffName() {
        return staffName;
    }

    public static String getStaffUnit() {
        return staffUnit;
    }

    public static String getStaffRole() {
        return staffRole;
    }
}
