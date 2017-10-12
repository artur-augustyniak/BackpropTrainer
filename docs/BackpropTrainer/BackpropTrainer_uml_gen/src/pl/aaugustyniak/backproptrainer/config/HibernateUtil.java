/**
 * Copyright 2013 Artur Augustyniak
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package pl.aaugustyniak.backproptrainer.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.SessionFactory;
import pl.aaugustyniak.backproptrainer.model.entities.PeceptronTest;
import pl.aaugustyniak.backproptrainer.model.entities.TrainingMseReadout;
import pl.aaugustyniak.backproptrainer.model.entities.ValidationMseReadout;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            Logger logger = Logger.getLogger("org.hibernate");
            logger.setLevel(Level.OFF);
            AnnotationConfiguration conf = new AnnotationConfiguration().configure("pl/aaugustyniak/backproptrainer/config/hibernate.cfg.xml"); //.buildSessionFactory();
            conf.setProperty("hibernate.dialect", AppPreferences.getHbDialect());
            conf.setProperty("hibernate.connection.driver_class", AppPreferences.getHbDriverClass());
            conf.setProperty("hibernate.connection.url", AppPreferences.getHbDsn());
            conf.setProperty("hibernate.connection.username", AppPreferences.getHbUser());
            conf.setProperty("hibernate.connection.password", AppPreferences.getHbPass());
            conf.setProperty("hibernate.cache.provider_class", AppPreferences.getHbCacheProvider());
            conf.setProperty("hibernate.hbm2ddl.auto", AppPreferences.getHbmDll());

            conf.addAnnotatedClass(ValidationMseReadout.class);
            conf.addAnnotatedClass(TrainingMseReadout.class);
            conf.addAnnotatedClass(PeceptronTest.class);

            sessionFactory = conf.buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {

        return sessionFactory;
    }
}
