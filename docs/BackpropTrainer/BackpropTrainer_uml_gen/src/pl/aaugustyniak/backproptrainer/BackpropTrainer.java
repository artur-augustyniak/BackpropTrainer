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
package pl.aaugustyniak.backproptrainer;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import pl.aaugustyniak.backproptrainer.cli.BpTrainCliTool;
import pl.aaugustyniak.backproptrainer.cli.ConfigCliTool;

/**
 * CLI frontend for batch ANN's testing
 *
 * @author Artur Augustyniak <artur@aaugustyniak.pl>
 */
public class BackpropTrainer {
    
    /**
     * CLI Dispatch
     *
     * @param args the command line arguments
     */
    @SuppressWarnings("static-access")
    public static void main(String[] args) {

        CommandLineParser parser = new BasicParser();
        HelpFormatter helper = new HelpFormatter();

        Options options = new Options();
        Option config = OptionBuilder
                .withArgName("action> <value")
                .withValueSeparator(' ')
                .hasArgs(2)
                .withDescription("manage config [list | set <key> <value>]")
                .create("config");
        options.addOption(config);

        Option bptrain = OptionBuilder
                .withArgName("runs num> <saving mode")
                .withValueSeparator(' ')
                .hasArgs(2)
                .withDescription("run tests [runs num <dry | saving>]")
                .create("bptrain");
        options.addOption(bptrain);
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("config")) {
                ConfigCliTool.doAction(line.getOptionValues("config"), line.getArgs());
            }
            if (line.hasOption("bptrain")) {
                BpTrainCliTool.doAction(line.getOptionValues("bptrain"));
            }
            if (line.getOptions().length == 0) {
                helper.printHelp("bptrain", options);
            }
        } catch (org.apache.commons.cli.ParseException ex) {
            helper.setWidth(120);
            helper.printHelp("bptrain", options);
        }
    }
}