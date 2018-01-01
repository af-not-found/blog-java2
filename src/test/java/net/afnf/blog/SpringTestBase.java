package net.afnf.blog;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class SpringTestBase {

    private static Logger logger = LoggerFactory.getLogger(SpringTestBase.class);

    @Autowired
    protected DataSource dataSource;

    static boolean initialized = true;

    @BeforeClass
    public static void beforeClass() throws Exception {
        initialized = false;
    }

    @Before
    public void before() throws Exception {
        if (initialized == false) {
            initialized = true;
            executeSql("/sql/db-schema.sql");
        }
    }

    protected void executeSql(String path) {
        logger.info("executeSql : " + path);

        Resource resource = new ClassPathResource(path, getClass());
        ResourceDatabasePopulator rdp = new ResourceDatabasePopulator();
        rdp.addScript(resource);
        rdp.setSqlScriptEncoding("UTF-8");
        rdp.setIgnoreFailedDrops(true);
        rdp.setContinueOnError(false);

        try (Connection conn = DataSourceUtils.getConnection(dataSource)) {
            rdp.populate(conn);
        }
        catch (Exception e) {
            throw new IllegalStateException("executeSql failed, path=" + path, e);
        }
    }
}
