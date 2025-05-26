package org.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<String> ragioniSocialiCertificate = FileReader.leggiFileRighe("RAGIONI SOCIALI CERTIFICATE.txt");
        List<String> ragioniSocialiInput = FileReader.leggiFileRighe("RAGIONI SOCIALI INPUT.txt");

        System.out.println("-----------------------------");
        for (int i = 0; i < ragioniSocialiCertificate.size(); i++) {
            String ragioneSocialeCertificata = ragioniSocialiCertificate.get(i);
            String ragioneSocialeInput = ragioniSocialiInput.get(i);
            System.out.println(ragioneSocialeInput + " -> " + ragioneSocialeCertificata);
            double dicePoints = StringsMatcher.getDicePoints(ragioneSocialeInput, ragioneSocialeCertificata);
            String dicePointsFormatted = String.format("%.2f", dicePoints);
            System.out.println(dicePointsFormatted);
        }
        System.out.println("-----------------------------");
    }
}