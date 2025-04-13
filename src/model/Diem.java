/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author trinh
 */
public class Diem {

    private String ma;
    private String ten;
    private float diemTiengAnh;
    private float diemTinHoc;
    private float diemGDTC;

    public Diem() {
    }

    public Diem(String ma, String ten, float diemTiengAnh, float diemTinHoc, float diemGDTC) {
        this.ma = ma;
        this.ten = ten;
        this.diemTiengAnh = diemTiengAnh;
        this.diemTinHoc = diemTinHoc;
        this.diemGDTC = diemGDTC;
    }

    public Diem(String ten, float diemTiengAnh, float diemTinHoc, float diemGDTC) {
        this.ten = ten;
        this.diemTiengAnh = diemTiengAnh;
        this.diemTinHoc = diemTinHoc;
        this.diemGDTC = diemGDTC;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public float getDiemTiengAnh() {
        return diemTiengAnh;
    }

    public void setDiemTiengAnh(float diemTiengAnh) {
        this.diemTiengAnh = diemTiengAnh;
    }

    public float getDiemTinHoc() {
        return diemTinHoc;
    }

    public void setDiemTinHoc(float diemTinHoc) {
        this.diemTinHoc = diemTinHoc;
    }

    public float getDiemGDTC() {
        return diemGDTC;
    }

    public void setDiemGDTC(float diemGDTC) {
        this.diemGDTC = diemGDTC;
    }

    public float getdiemTB() {
        return (diemTiengAnh + diemTinHoc + diemGDTC) / 3;
    }

    public Object[] toRowtable() {
        return new Object[]{ma, ten, diemTiengAnh, diemTinHoc, diemGDTC, getdiemTB()};
    }
}
