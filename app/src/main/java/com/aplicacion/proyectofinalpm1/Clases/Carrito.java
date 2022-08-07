package com.aplicacion.proyectofinalpm1.Clases;

public class Carrito {

    private String id;
    private String NomProBebes;
    private String cantidadPañales;
    private String precioPañales;
    private String NomProBebidas;
    private String cantidadBebidas;
    private String precioBebidas;
    private String NomProCarnes;
    private String cantidadCarnes;
    private String precioCarnes;
    private String NomProGranosB;
    private String cantidadGranosB;
    private String precioGranosB;
    private String NomProLacteos;
    private String cantidadLacteos;
    private String precioLacteos;
    private String subtotal;
    private String impuesto;
    private String total;

    public Carrito(){

    }

    public Carrito(String id, String nomProBebes, String cantidadPañales, String precioPañales, String nomProBebidas, String cantidadBebidas, String precioBebidas, String nomProCarnes, String cantidadCarnes, String precioCarnes, String nomProGranosB, String cantidadGranosB, String precioGranosB, String nomProLacteos, String cantidadLacteos, String precioLacteos, String subtotal, String impuesto, String total) {
        this.id = id;
        this.NomProBebes = nomProBebes;
        this.cantidadPañales = cantidadPañales;
        this.precioPañales = precioPañales;
        this.NomProBebidas = nomProBebidas;
        this.cantidadBebidas = cantidadBebidas;
        this.precioBebidas = precioBebidas;
        this.NomProCarnes = nomProCarnes;
        this.cantidadCarnes = cantidadCarnes;
        this.precioCarnes = precioCarnes;
        this.NomProGranosB = nomProGranosB;
        this.cantidadGranosB = cantidadGranosB;
        this.precioGranosB = precioGranosB;
        this.NomProLacteos = nomProLacteos;
        this.cantidadLacteos = cantidadLacteos;
        this.precioLacteos = precioLacteos;
        this.subtotal = subtotal;
        this.impuesto = impuesto;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomProBebes() {
        return NomProBebes;
    }

    public void setNomProBebes(String nomProBebes) {
        NomProBebes = nomProBebes;
    }

    public String getCantidadPañales() {
        return cantidadPañales;
    }

    public void setCantidadPañales(String cantidadPañales) {
        this.cantidadPañales = cantidadPañales;
    }

    public String getPrecioPañales() {
        return precioPañales;
    }

    public void setPrecioPañales(String precioPañales) {
        this.precioPañales = precioPañales;
    }

    public String getNomProBebidas() {
        return NomProBebidas;
    }

    public void setNomProBebidas(String nomProBebidas) {
        NomProBebidas = nomProBebidas;
    }

    public String getCantidadBebidas() {
        return cantidadBebidas;
    }

    public void setCantidadBebidas(String cantidadBebidas) {
        this.cantidadBebidas = cantidadBebidas;
    }

    public String getPrecioBebidas() {
        return precioBebidas;
    }

    public void setPrecioBebidas(String precioBebidas) {
        this.precioBebidas = precioBebidas;
    }

    public String getNomProCarnes() {
        return NomProCarnes;
    }

    public void setNomProCarnes(String nomProCarnes) {
        NomProCarnes = nomProCarnes;
    }

    public String getCantidadCarnes() {
        return cantidadCarnes;
    }

    public void setCantidadCarnes(String cantidadCarnes) {
        this.cantidadCarnes = cantidadCarnes;
    }

    public String getPrecioCarnes() {
        return precioCarnes;
    }

    public void setPrecioCarnes(String precioCarnes) {
        this.precioCarnes = precioCarnes;
    }

    public String getNomProGranosB() {
        return NomProGranosB;
    }

    public void setNomProGranosB(String nomProGranosB) {
        NomProGranosB = nomProGranosB;
    }

    public String getCantidadGranosB() {
        return cantidadGranosB;
    }

    public void setCantidadGranosB(String cantidadGranosB) {
        this.cantidadGranosB = cantidadGranosB;
    }

    public String getPrecioGranosB() {
        return precioGranosB;
    }

    public void setPrecioGranosB(String precioGranosB) {
        this.precioGranosB = precioGranosB;
    }

    public String getNomProLacteos() {
        return NomProLacteos;
    }

    public void setNomProLacteos(String nomProLacteos) {
        NomProLacteos = nomProLacteos;
    }

    public String getCantidadLacteos() {
        return cantidadLacteos;
    }

    public void setCantidadLacteos(String cantidadLacteos) {
        this.cantidadLacteos = cantidadLacteos;
    }

    public String getPrecioLacteos() {
        return precioLacteos;
    }

    public void setPrecioLacteos(String precioLacteos) {
        this.precioLacteos = precioLacteos;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(String impuesto) {
        this.impuesto = impuesto;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
