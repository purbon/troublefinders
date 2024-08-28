package com.purbon.kafka;

public class Protocol {


    /** Server:
     * It’s not DNS
     * There’s no way it’s DNS
     * It was DNS
     */

    /** Client
     * BOFH we have a problem, can you help me?.
     * Are you sure?
     * Can you ping the machine, please?
     */


    public static String nextClientMessage(int order) {
        String message = "";
        switch (order) {
            case 0:
               message = "BOFH we have a problem, can you help me?";
               break;
            case 1:
                message = "Are you sure?";
                break;
            case 2:
                message = "Can you ping the machine, please?";
                break;
            case 3:
                message  = "Thanks a lot. Bye!";
                break;
            default:
                throw new RuntimeException("no, no!");
        }
        return message;
    }

    public static String nextServerMessage(int order) {
        String message = "";
        switch (order) {
            case 0:
                message = "It’s not DNS";
                break;
            case 1:
                message = "There’s no way it’s DNS";
                break;
            case 2:
                message = "It was DNS";
                break;
            default:
                throw new RuntimeException("no, no!");
        }
        return message;
    }
}
