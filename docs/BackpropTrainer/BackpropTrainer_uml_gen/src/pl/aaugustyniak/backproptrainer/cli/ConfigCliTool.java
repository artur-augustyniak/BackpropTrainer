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
package pl.aaugustyniak.backproptrainer.cli;

import java.util.Iterator;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import org.apache.commons.cli.ParseException;
import pl.aaugustyniak.backproptrainer.config.AppPreferences;

/**
 * CLI Tool for managing app configuration
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class ConfigCliTool {

    private enum Actions {

        LIST,
        SET
    }

    /**
     * Proceed with action based on parsed params Throws ParseException if it
     * can't do nothing with given combination of options and params
     *
     * @param optionValues
     * @throws ParseException
     */
    public static void doAction(String[] optionValues, String[] args) throws ParseException {
        String optionValue = optionValues[0];
        try {
            switch (ConfigCliTool.Actions.valueOf(optionValue.toUpperCase())) {
                case LIST:
                    listOptions();
                    break;
                case SET:
                    setOption(optionValues[1], args[0]);
                    break;
            }
        } catch (Exception ex) {
            throw new ParseException(optionValue);
        }
    }

    /**
     * List all User node properties wrapped in class AppPreferences
     *
     * @throws BackingStoreException
     */
    private static void listOptions() throws BackingStoreException {
        System.out.println("Current config is:");
        System.out.println("CWD = " + System.getProperty("user.dir"));
        Iterator it = AppPreferences.getCurrentPrefs().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            it.remove();
        }
    }

    /**
     * Set known config values
     *
     * @param optionParam
     * @param paramValue
     * @throws BackingStoreException
     * @throws ParseException
     */
    private static void setOption(String optionParam, String paramValue) throws BackingStoreException, ParseException {
        String key = AppPreferences.getCurrentPrefs().get(optionParam);
        if (key != null) {
            AppPreferences.getRawStorage().put(optionParam, paramValue);
            System.out.println("Done!");
            listOptions();
        } else {
            throw new ParseException(paramValue);
        }
    }
}
