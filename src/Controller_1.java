package mg.itu.prom16.controller;

import mg.itu.prom16.annotation.*;

@AnnotationController
public class Controller_1 {
    @GET("/MappingCotrollers1")
    public String IssetGet_controller_1() {
        return ("IssetGet_Controller_1 executed successfully");
    }


    public void NotIssetGet_controller_1() {
    }
}
