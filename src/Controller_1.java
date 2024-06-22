package mg.itu.prom16.controller;

import mg.itu.prom16.annotation.*;

import java.util.HashMap;

import modelandview.*;

@AnnotationController
public class Controller_1 {
    // @GET("/MappingCotrollers1")
    // public String IssetGet_controller_1() {
    // return ("IssetGet_Controller_1 executed successfully");
    // }

    @GET("/MappingModelAndView")
    public ModelAndView ModelAndView() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("greating", "Hello World");
        return new ModelAndView("/", data);
    }

    public void NotIssetGet_controller_1() {
    }
}
