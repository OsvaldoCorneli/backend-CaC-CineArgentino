package com.incaaapi;

public class Persona {

    int id;
    String name;
    String description;
    String wikiLink;
    String image;

public Persona(){}

public Persona(int id, String name, String description, String wikiLink, String image){
    this.id = id;
    this.name = name;
    this.description = description;
    this.wikiLink = wikiLink;
    this.image = image;

}

public int getId(){
    return id;
}

public String getName(){
    return name;
}

public String getDescription(){
    return description;
}

public String getwikiLink(){
    return wikiLink;
}

public String getImage(){
    return image;
}
    
}
