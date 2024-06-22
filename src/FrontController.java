package mg.itu.prom16;

import mg.itu.prom16.mapping.*;
import mg.itu.prom16.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import modelandview.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.ClassNotFoundException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@WebServlet(name = "FrontController", urlPatterns = { "/" })
public class FrontController extends HttpServlet {
    public String packageName;
    public List<String> controllerNames = new ArrayList<>();

    public HashMap<String, Mapping> Mappings = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.packageName = config.getInitParameter("NameControllerPackage");
        this.ScanController(packageName);

    }

    protected void ProcessRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = request.getRequestURL().toString();
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<html><head>");
        out.println("<title>Servlet FrontController</title>");
        out.println("</head><body>");
        out.println("<p>" + url + "</p>");
        out.println("<p>Nom du package: " + packageName + "</p>");
        if (controllerNames.isEmpty()) {
            out.println("tsy misy controller ao @ " + packageName);
        } else {
            if (this.Mappings.size() == 0) {
                out.println("<h2>Aucune methodes annotatée Get</h2> ");
            } else {
                out.println("<h2>Les Mapping</h2>");
                for (Map.Entry<String, Mapping> entry : Mappings.entrySet()) {
                    // affichage de l'URL
                    out.println("<p>Url: " + entry.getKey() + "</p>");
                    // affichage de la classe
                    out.println("<p>className: " + entry.getValue().getClassName() + "</p>");
                    // affichage de la methode
                    out.println("<p>   methodName: " + entry.getValue().getMethodName() + "</p>");
                    // recuperation de la classe par son nom
                    try {
                        Class<?> classe = Class.forName(entry.getValue().getClassName());
                        // recup de la method par son nom
                        Method method = classe.getDeclaredMethod(entry.getValue().getMethodName());
                        // invocation de la methode
                        Object classInstance = classe.getDeclaredConstructor().newInstance();
                        Object Result = method.invoke(classInstance);
                        this.ControlleData(out, Result, request, response);
                        out.println("<hr/>");
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }
            }
        }

        out.println("</body></html>");
        out.close();
    }

    public void ControlleData(PrintWriter out, Object result, HttpServletRequest request,
            HttpServletResponse response) {
        try {

            if (result instanceof String) {
                out.println("<p> Retour de la methode" + (String) result + "</p>");
            } else if (result instanceof ModelAndView) {
                ModelAndView model = (ModelAndView) result;
                for (Map.Entry<String, Object> entry : model.getData().entrySet()) {
                    String name = entry.getKey();
                    Object value = entry.getValue();
                    request.setAttribute(name, value);
                }
                RequestDispatcher requestdispatcher = request.getRequestDispatcher(model.getUrl());
                // requestdispatcher.forward(request, response);
            } else {
                out.println("non reconnuer");
            }
        } catch (Exception e) {
            e.printStackTrace(out);
            // TODO: handle exception
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProcessRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProcessRequest(request, response);
    }

    public void ScanController(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');

            URL resource = classLoader.getResource(path);

            URI resourceUri = resource.toURI();
            Path classPath = Paths.get(resourceUri);

            try (Stream<Path> paths = Files.walk(classPath)) {
                paths.filter(f -> f.toString().endsWith(".class"))
                        .forEach(f -> {
                            String className = packageName + "." + f.getFileName().toString().replace(".class", "");
                            try {
                                Class<?> clazz = Class.forName(className);
                                if (clazz.isAnnotationPresent(AnnotationController.class)
                                        && !Modifier.isAbstract(clazz.getModifiers())) {
                                    controllerNames.add(clazz.getSimpleName());
                                    Method[] methods = clazz.getDeclaredMethods();
                                    for (Method method : methods) {
                                        if (method.isAnnotationPresent(GET.class)) {
                                            GET annotation = method.getAnnotation(GET.class);
                                            String url = annotation.value();
                                            Mapping mapping = new Mapping(clazz.getName(), method.getName());
                                            this.Mappings.put(url, mapping);
                                        }
                                    }
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
