package com.purbon.kafka;

import com.purbon.kafka.cmd.DNSLookupCmd;
import picocli.CommandLine.*;
import picocli.CommandLine;
import picocli.CommandLine.Model.*;
public class App {

    @Command(name = "deep-tools", subcommands = { DNSLookupCmd.class,
            CommandLine.HelpCommand.class }, description = "Some tools to help you with troubleshooting deployments")
    static class ParentCommand implements Runnable {

        @Spec
        CommandSpec spec;

        @Override
        public void run() {
            spec.commandLine().usage(System.err);
        }
    }

    public static void main(String[] args) {
        new CommandLine(new ParentCommand())
                .execute(args);
    }
}
