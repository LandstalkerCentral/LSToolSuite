/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsc.ls.text.io;

import com.lsc.ls.text.TextManager;
import com.lsc.ls.text.compression.TextDecoder;
import com.lsc.ls.text.compression.TextEncoder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class DisassemblyManager {
    
    public static final String HUFFMANTREEOFFSETS_FILENAME = "huffmantreeoffsets.bin";
    public static final String HUFFMANTREES_FILENAME = "huffmantrees.bin";
    public static final String TEXTBANK_FILENAME = "textbankX.bin";  
    
    public static String[] importDisassembly(String basePath){
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.importDisassembly() - Importing disassembly ...");
        DisassemblyManager.parseOffsets(basePath);
        DisassemblyManager.parseTrees(basePath);
        String[] gamescript = DisassemblyManager.parseAllTextbanks(basePath);        
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.importDisassembly() - Disassembly imported.");
        return gamescript;
    }
    
    public static void exportDisassembly(String[] gamescript, String basePath){
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.exportDisassembly() - Exporting disassembly ...");
        DisassemblyManager.produceTrees(gamescript);
        DisassemblyManager.produceTextbanks(gamescript);
        DisassemblyManager.writeFiles(basePath);
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.exportDisassembly() - Disassembly exported.");        
    }    
    
    private static void parseOffsets(String basePath){
        try{
            System.out.println("com.lsc.ls.text.io.DisassemblyManager.parseOffsets() - Parsing offsets ...");
            Path path = Paths.get(basePath + HUFFMANTREEOFFSETS_FILENAME);
            byte[] data = Files.readAllBytes(path);
            TextDecoder.parseOffsets(data);
            System.out.println("com.lsc.ls.text.io.DisassemblyManager.parseOffsets() - Offsets parsed.");
        } catch(IOException e){
            System.err.println("com.lsc.ls.text.io.DisassemblyManager.parseOffsets() - Error while parsing huffmanTreeOffsetsFile data : "+e);
        }
    }
    
    private static void parseTrees(String basePath){
        try{
            System.out.println("com.lsc.ls.text.io.DisassemblyManager.parseTrees() - Parsing trees ...");
            Path path = Paths.get(basePath + HUFFMANTREES_FILENAME);
            byte[] data = Files.readAllBytes(path);
            TextDecoder.parseTrees(data);
            System.out.println("com.lsc.ls.text.io.DisassemblyManager.parseTrees() - Trees parsed.");            
        } catch(IOException e){
            System.err.println("com.lsc.ls.text.io.DisassemblyManager.parseTrees() - Error while parsing huffmanTreesFile data : "+e);
        }
    }
    
    private static String[] parseAllTextbanks(String basePath){
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.parseTextbank() - Parsing textbank ...");
        String[] gamescript = new String[0];        
        try{
            for(int i=0;i<100;i++){
                String index = String.format("%01d", i);
                Path path = Paths.get(basePath + TEXTBANK_FILENAME.replace("X.bin", index+".bin"));
                byte[] data = Files.readAllBytes(path); 
                String[] textbankStrings = TextDecoder.parseTextbank(data, i);
                String[] workingStringArray = Arrays.copyOf(gamescript, gamescript.length + textbankStrings.length);
                System.arraycopy(textbankStrings, 0, workingStringArray, gamescript.length, textbankStrings.length);
                gamescript = workingStringArray;
            }
        }catch(IOException e){
            System.out.println("No more textbank files to parse.");
        }catch(Exception e){
             System.err.println("com.lsc.ls.text.io.DisassemblyManager.parseTextbank() - Error while parsing textbankFile data : "+e);
        } 
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.parseTextbank() - Textbanks all parsed.");
        return gamescript;
    }
    
    private static void produceTrees(String[] gamescript) {
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.produceTrees() - Producing trees ...");
        TextEncoder.produceTrees(gamescript);
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.produceTrees() - Trees produced.");
    }

    private static void produceTextbanks(String[] gamescript) {
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.produceTextbanks() - Producing text banks ...");
        TextEncoder.produceTextbanks(gamescript);
        System.out.println("com.lsc.ls.text.io.DisassemblyManager.produceTextbanks() - Text banks produced.");
    }    
  
    private static void writeFiles(String basePath){
        try {
            System.out.println("com.lsc.ls.text.io.DisassemblyManager.writeFiles() - Writing files ...");
            Path offsetsFilePath = Paths.get(basePath + HUFFMANTREEOFFSETS_FILENAME);
            Path treesFilePath = Paths.get(basePath + HUFFMANTREES_FILENAME);
            Path testbankFilePath = Paths.get(basePath + TEXTBANK_FILENAME);
            byte[] newHuffmantreeOffsetsFileBytes = TextEncoder.getNewHuffmantreeOffsetsFileBytes();
            byte[] newHuffmanTreesFileBytes = TextEncoder.getNewHuffmanTreesFileBytes();
            byte[][] newTextbanks = TextEncoder.getNewTextbanks();
            Files.write(offsetsFilePath,newHuffmantreeOffsetsFileBytes);
            System.out.println(newHuffmantreeOffsetsFileBytes.length + " bytes into " + offsetsFilePath);
            Files.write(treesFilePath, newHuffmanTreesFileBytes);
            System.out.println(newHuffmanTreesFileBytes.length + " bytes into " + treesFilePath);
            for(int i=0;i<newTextbanks.length;i++){
                String index = String.format("%01d", i);
                Path textbankFilePath = Paths.get(testbankFilePath.toString().replace("X.bin", index+".bin"));
                Files.write(textbankFilePath, newTextbanks[i]);
                System.out.println(newTextbanks[i].length + " bytes into " + textbankFilePath);
            }
            System.out.println("com.lsc.ls.text.io.DisassemblyManager.writeFiles() - Files written.");
        } catch (IOException ex) {
            Logger.getLogger(TextManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    
}
