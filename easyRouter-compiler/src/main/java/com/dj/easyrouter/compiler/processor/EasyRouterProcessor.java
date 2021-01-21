package com.dj.easyrouter.compiler.processor;

import com.dj.easyrouter.annotation.EasyRoute;
import com.dj.easyrouter.compiler.utils.Constant;
import com.dj.easyrouter.compiler.utils.LogUtils;
import com.dj.easyrouter.compiler.utils.Utils;
import com.dj.easyrouter.model.EasyRouteMeta;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
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
    private String moduleName;
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
    /**
     * 文件工具类
     */
    private Filer filerUtils;
    /**
     * 日志工具类
     */
    private LogUtils logUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Map<String,String> options = processingEnv.getOptions();
        moduleName = options.get("moduleName");

        logUtils = LogUtils.newLog(processingEnv.getMessager());
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filerUtils = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!Utils.isEmpty(annotations)) {
            //被Route注解的节点集合(取得所有修饰了@EasyRoute的元素)
            Set<? extends Element> rootElements = roundEnv.getElementsAnnotatedWith(EasyRoute.class);
            if (!Utils.isEmpty(rootElements)) {
                processorRoute(rootElements);
            }
            return true;
        }
        return false;
    }

    /**
     * 解析生成java类
     */
    private void processorRoute(Set<? extends Element> rootElements) {
        //获得Activity这个类的节点信息
        TypeElement activity = elementUtils.getTypeElement(Constant.ACTIVITY);
        TypeElement service = elementUtils.getTypeElement(Constant.SERVICE);
        TypeElement fragment = elementUtils.getTypeElement(Constant.FRAGMENT);
        TypeElement fragment_v4 = elementUtils.getTypeElement(Constant.FRAGMENT_V4);

        for (Element element : rootElements) {
            EasyRouteMeta routeMeta;

            //类信息
            TypeMirror typeMirror = element.asType();
            EasyRoute route = element.getAnnotation(EasyRoute.class);
            if (typeUtils.isSubtype(typeMirror, activity.asType())) {
                routeMeta = new EasyRouteMeta(EasyRouteMeta.Type.ACTIVITY, route, element);
            } else if (typeUtils.isSubtype(typeMirror, service.asType())) {
                routeMeta = new EasyRouteMeta(EasyRouteMeta.Type.SERVICE, route, element);
            } else if (typeUtils.isSubtype(typeMirror, fragment.asType()) || typeUtils.isSubtype(typeMirror, fragment_v4.asType())) {
                routeMeta = new EasyRouteMeta(EasyRouteMeta.Type.FRAGMENT, route, element);
            }
            else {
                throw new RuntimeException("Just support Activity or Service or fragment Route: " + element);
            }
            categories(routeMeta);
        }

        TypeElement iRouteGroup = elementUtils.getTypeElement(Constant.IROUTE_GROUP);
        TypeElement iRouteRoot = elementUtils.getTypeElement(Constant.IROUTE_ROOT);

        //生成Group记录分组表
        generatedGroup(iRouteGroup);

        //生成Root类 作用：记录<分组，对应的Group类>
        generatedRoot(iRouteRoot, iRouteGroup);
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

    /**
     * 生成分组表
     * @param iRouteGroup
     */
    private void generatedGroup(TypeElement iRouteGroup) {
        //创建参数类型 Map<String, EasyRouteMeta>
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(EasyRouteMeta.class));
        ParameterSpec altas = ParameterSpec.builder(parameterizedTypeName, "atlas").build();

        for (Map.Entry<String, List<EasyRouteMeta>> entry : groupMap.entrySet()) {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_LOAD_INFO)
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addParameter(altas);

            String groupName = entry.getKey();
            List<EasyRouteMeta> groupData = entry.getValue();
            for (EasyRouteMeta routeMeta : groupData) {
                //函数体的添加
                methodBuilder.addStatement("atlas.put($S,$T.build($T.$L,$T.class,$S,$S))",
                        routeMeta.getPath(),
                        ClassName.get(EasyRouteMeta.class),
                        ClassName.get(EasyRouteMeta.Type.class),
                        routeMeta.getType(),
                        ClassName.get(((TypeElement) routeMeta.getElement())),
                        routeMeta.getPath(),
                        routeMeta.getGroup());
            }
            String groupClassName = Constant.NAME_OF_GROUP + groupName;
            TypeSpec typeSpec = TypeSpec.classBuilder(groupClassName)
                    .addSuperinterface(ClassName.get(iRouteGroup))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();
            JavaFile javaFile = JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build();
            try {
                javaFile.writeTo(filerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }
            rootMap.put(groupName, groupClassName);
        }
    }

    /**
     * 生成Root类  作用：记录<分组，对应的Group类>
     * @param iRouteRoot
     * @param iRouteGroup
     */
    private void generatedRoot(TypeElement iRouteRoot, TypeElement iRouteGroup) {
        //创建参数类型 Map<String,Class<? extends IRouteGroup>> routes>
        //Wildcard 通配符
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(iRouteGroup))
                ));
        //参数 Map<String,Class<? extends IRouteGroup>> routes> routes
        ParameterSpec parameter = ParameterSpec.builder(parameterizedTypeName, "routes").build();
        //函数 public void loadInfo(Map<String,Class<? extends IRouteGroup>> routes> routes)
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constant.METHOD_LOAD_INFO)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(parameter);
        //函数体
        for (Map.Entry<String, String> entry : rootMap.entrySet()) {
            methodBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(Constant.PACKAGE_OF_GENERATE_FILE, entry.getValue()));
        }
        //生成$Root$类
        String className = Constant.NAME_OF_ROOT + moduleName;
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(iRouteRoot))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        try {
            JavaFile.builder(Constant.PACKAGE_OF_GENERATE_FILE, typeSpec).build().writeTo(filerUtils);
            logUtils.i("Generated RouteRoot：" + Constant.PACKAGE_OF_GENERATE_FILE + "." + className);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}