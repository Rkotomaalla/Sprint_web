package mg.itu.prom16;

import mg.itu.prom16.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.ClassNotFoundException;
import java.lang.reflect.Modifier;

@WebServlet(name = "FrontController", urlPatterns = { "/" })
public class FrontController extends HttpServlet {
    public String packageName;
    public boolean ifControllerScanned = false;
    public List<String> controllerNames = new ArrayList<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.packageName = config.getInitParameter("NameControllerPackage");
    }

    protected void ProcessRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String url = request.getRequestURL().toString();
        PrintWriter out = response.getWriter();
        if (!ifControllerScanned) {
            this.ScanController(packageName, out);
        }
        response.setContentType("text/html");
        out.println("<html><head>");
        out.println("<title>Servlet FrontController</title>");
        out.println("</head><body>");
        out.println("<p>" + url + "</p>");
        out.println("<p>Nom du package: " + packageName + "</p>");
        if (controllerNames.isEmpty()) {
            out.println("tsy misy controller ao @ " + packageName);
        } else {
            out.println("les controller trouves:");
            for (String controller : controllerNames) {
                out.println(controller);
            }
        }

        out.println("</body></html>");
        out.close();
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

    public void ScanController(String packageName, PrintWriter out) {
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
                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace(out);
                            }
                        });
            }

            ifControllerScanned = false;
        } catch (Exception e) {
            e.printStackTrace(out);
        }
    }

}
