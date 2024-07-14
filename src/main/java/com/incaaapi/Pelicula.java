package com.incaaapi;

public class Pelicula{

    // Estos son los atritubos de la clase pelicula igual que a las columnas de la tabla
    private int idpelicula;
    private String title;
    private String estreno;
    private String duracion;
    private String sinopsis;
    private String image;
    private String elenco;
    private String director;
    private String trailer;
    private String wikiLink;

    // los constructores

    // este esta vacio para deserializacion de JSON

    public Pelicula(){}

    public Pelicula(int idpelicula, String title,String estreno,String duracion,String sinopsis,String image,String elenco,String director,String trailer,String wikiLink){

        this.idpelicula = idpelicula;
        this.title = title;
        this.estreno = estreno;
        this.duracion = duracion;
        this.sinopsis = sinopsis;
        this.image = image;
        this.elenco = elenco;
        this.director = director;
        this.trailer = trailer;
        this.wikiLink = wikiLink;

    }

    public int getIdPelicula(){
        return idpelicula;
    }

    public String getTitle(){
        return title;
    }

    public String getEstreno(){
        return estreno;
    }
    public String getDuracion(){
        return duracion;
    }

    public String getSinopsis(){
        return sinopsis;
    }


    public String getImage(){
        return image;
    }

    public String getElenco(){
        return elenco;
    }

    public String getDirector(){
        return director;
    }

    public String getTrailer(){
        return trailer;
    }

    public String getWikiLink(){
        return wikiLink;
    }

    






}