package partner42.moduleapi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import partner42.moduleapi.config.JpaPackage.JpaAndEntityPackagePathConfig;
import partner42.moduleapi.config.TestBootstrapConfig;
import partner42.modulecommon.config.BootstrapDataLoader;
import partner42.modulecommon.config.jpa.Auditor;
import partner42.modulecommon.config.querydsl.QuerydslConfig;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ServiceWithDaoTest.CustomImportSelector.class,
    Auditor.class, QuerydslConfig.class, JpaAndEntityPackagePathConfig.class,
    TestBootstrapConfig.class, BootstrapDataLoader.class})
public @interface ServiceWithDaoTest {
    Class<?>[] value() ;

    public class CustomImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata metadata) {
            AnnotationAttributes attrs = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(ServiceWithDaoTest.class.getName(), true));

            Class<?>[] classes = attrs.getClassArray("value");
            List<String> classNames = new ArrayList<>();
            for (Class<?> clazz : classes) {
                if (Configuration.class.isAssignableFrom(clazz)) {
                    classNames.add(clazz.getName());
                } else {
                    ComponentScan scan = clazz.getAnnotation(ComponentScan.class);
                    if (scan != null) {
                        classNames.addAll(Arrays.asList(scan.basePackages()));
                        classNames.addAll(Arrays.asList(Arrays.stream(scan.basePackageClasses()).map(Class::getName).toArray(String[]::new)));
                    }
                    classNames.add(clazz.getName());
                }
            }
            return classNames.toArray(new String[0]);
        }
    }
}
