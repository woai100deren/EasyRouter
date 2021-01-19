package com.dj.easyrouter.compiler.processor;

import com.dj.easyrouter.annotation.EasyRoute;
import com.dj.easyrouter.compiler.modle.EasyRouteMeta;
import com.dj.easyrouter.compiler.utils.Constant;
import com.dj.easyrouter.compiler.utils.LogUtils;
import com.dj.easyrouter.compiler.utils.Utils;
import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 注解处理器
 */
//只关心我们自己的注解，所以加上SupportedAnnotationTypes
@SupportedAnnotationTypes(Constant.ANNOTATION_TYPE_ROUTE)
//支持 moduleName的参数传递
@SupportedOptions(Constant.ARGUMENTS_NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
//注册处理器
@AutoService(Processor.class)
public class EasyRouterProcessor extends AbstractProcessor {
//    private String moduleName;
    /**
     * key:组名 value:类名
     */
    private Map<String, String> rootMap = new TreeMap<>();
    /**
     * 分组 key:组名 value:对应组的路由信息
     */
    private Map<String, List<EasyRouteMeta>> groupMap = new HashMap<>();
    /**
     * 节点工具类 (类、函数、属性都是节点)
     */
    private Elements elementUtils;
    /**
     * type(类信息)工具类
     */
    private Types typeUtils;
    private LogUtils logUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
//        Map<String,String> options = processingEnv.getOptions();
//        moduleName = options.get("moduleName");

        logUtils = LogUtils.newLog(processingEnv.getMessager());
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!Utils.isEmpty(annotations)) {
            //被Route注解的节点集合
            Set<? extends Element> rootElements = roundEnv.getElementsAnnotatedWith(EasyRoute.class);
            if (!Utils.isEmpty(rootElements)) {
                processorRoute(rootElements);
            }
            return true;
        }
        return false;

//        String code = "package com.dj.easyrouter.routers;\n" +
//                "\n" +
//                "import android.app.Activity;\n" +
//                "\n" +
//                "import com.dj.easyrouter.inter.IRouterLoad;\n" +
//                "import com.dj.easyrouter.simple.MainActivity;\n" +
//                "\n" +
//                "import java.util.Map;\n" +
//                "\n" +
//                "public class "+moduleName+"Router implements IRouterLoad {\n" +
//                "    @Override\n" +
//                "    public void loadInfo(Map<String, Class<? extends Activity>> routers) {\n" +
//                "        routers.put(\"/asdasf/asfdsfe\", MainActivity.class);\n" +
//                "    }\n" +
//                "}";
//
//        //文件工具
//        Filer filer = processingEnv.getFiler();
//        try {
//            //创建源文件
//            JavaFileObject sourceObject = filer.createSourceFile("com.dj.easyrouter.routers.APPRouter");
//            Writer writer = sourceObject.openWriter();
//            writer.write(code);
//            writer.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return false;
    }

    /**
     * 解析生成java类
     */
    private void processorRoute(Set<? extends Element> rootElements) {
        //获得Activity这个类的节点信息
        TypeElement activity = elementUtils.getTypeElement(Constant.ACTIVITY);
        TypeElement service = elementUtils.getTypeElement(Constant.ISERVICE);

        for (Element element : rootElements) {
            EasyRouteMeta routeMeta;

            //类信息
            TypeMirror typeMirror = element.asType();
            EasyRoute route = element.getAnnotation(EasyRoute.class);
            if (typeUtils.isSubtype(typeMirror, activity.asType())) {
                routeMeta = new EasyRouteMeta(EasyRouteMeta.Type.ACTIVITY, route, element);
            } else if (typeUtils.isSubtype(typeMirror, service.asType())) {
                routeMeta = new EasyRouteMeta(EasyRouteMeta.Type.ISERVICE, route, element);
            }
            else {
                throw new RuntimeException("Just support Activity or IService Route: " + element);
            }

            categories(routeMeta);
        }
    }
    /**
     * 检查是否配置 group 如果没有配置 则从path截取出组名
     * @param easyRouteMeta
     */
    private void categories(EasyRouteMeta easyRouteMeta){
        if (routeVerify(easyRouteMeta)) {
            logUtils.i("Group : " + easyRouteMeta.getGroup() + " path=" + easyRouteMeta.getPath());
            //分组与组中的路由信息
            List<EasyRouteMeta> routeMetas = groupMap.get(easyRouteMeta.getGroup());
            if (Utils.isEmpty(routeMetas)) {
                routeMetas = new ArrayList<>();
                routeMetas.add(easyRouteMeta);
                groupMap.put(easyRouteMeta.getGroup(), routeMetas);
            } else {
                routeMetas.add(easyRouteMeta);
            }
        } else {
            logUtils.i("Group info error:" + easyRouteMeta.getPath());
        }
    }

    /**
     * 验证path路由地址的合法性
     * @param routeMeta
     * @return
     */
    private boolean routeVerify(EasyRouteMeta routeMeta) {
        String path = routeMeta.getPath();
        String group = routeMeta.getGroup();
        // 必须以 / 开头来指定路由地址
        if (!path.startsWith("/")) {
            return false;
        }
        //如果group没有设置 我们从path中获得group
        if (Utils.isEmpty(group)) {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            //截取出的group还是空
            if (Utils.isEmpty(defaultGroup)) {
                return false;
            }
            routeMeta.setGroup(defaultGroup);
        }
        return true;
    }
}