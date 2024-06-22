package mg.itu.prom16.controller;

import mg.itu.prom16.annotation.*;

@AnnotationController
public class Controller_2 {
    @GET("/MappingCotrollers2")
    public String IssetGet_controller_2() {
        return ("IssetGet_Controller_2 executed successfully");
    }

    public void NotIssetGet_controller_2() {
    }
}
