/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsc.ls.text.io;

import com.lsc.ls.text.TextManager;
import com.lsc.ls.text.compression.TextDecoder;
import com.lsc.ls.text.compression.TextEncoder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class RomManager {
    
    private static final int LINES_PER_BANK = 256;
    
    public static final int ORIGINAL_ROM_TYPE = 0;
    public static final int ALTERNATE_ROM_TYPE = 1; // Keeping principle from SF2's CARAVAN_ROM_TYPE just in case.
    
    private static final int[][] HUFFMANTREEOFFSETS_OFFSETS = {   {0x23D60,0x23E38},
                                                            {0x23D60,0x23E38}
                                                        };
    private static final int[][] HUFFMANTREES_OFFSETS = { {0x23E38,0x2469C},
                                                    {0x23E38,0x2469C}
                                                    };
    private static final int[][] TEXTBANKS_OFFSETS = {{   0x2B27A,0x2C29B,0x2DCC8,0x2F787,
                                                    0x3153E,0x330AB,0x34830,0x36087,
                                                    0x377E3,0x38368},
                                                {   0x2B27A,0x2C29B,0x2DCC8,0x2F787,
                                                    0x3153E,0x330AB,0x34830,0x36087,
                                                    0x377E3,0x38368}
                                                };
    
    private static File romFile;  
    private static byte[] romData;
    
    public static String[] importRom(int romType, String romFilePath, int lastLineIndex){
        System.out.println("com.lsc.ls.text.io.RomManager.importRom() - Importing ROM ...");
        RomManager.openFile(romFilePath);
        RomManager.parseOffsets(romType);
        RomManager.parseTrees(romType);
        String[] textlines = RomManager.parseAllTextbanks(romType, lastLineIndex);        
        System.out.println("com.lsc.ls.text.io.RomManager.importRom() - ROM imported.");
        return textlines;
    }
    
    public static void exportRom(int romType, String[] textlines, String romFilePath){
        System.out.println("com.lsc.ls.text.io.RomManager.exportRom() - Exporting ROM ...");
        RomManager.produceTrees(textlines);
        RomManager.produceTextbanks(textlines);
        RomManager.writeFile(romType, romFilePath);
        System.out.println("com.lsc.ls.text.io.RomManager.exportRom() - ROM exported.");        
    }    
    
    private static void openFile(String romFilePath){
        try {
            System.out.println("com.lsc.ls.text.io.RomManager.openFiles() - ROM file path : " + romFilePath);
            romFile = new File(romFilePath);
            romData = Files.readAllBytes(Paths.get(romFile.getAbsolutePath()));
            System.out.println("com.lsc.ls.text.io.RomManager.openFiles() - File opened.");
        } catch (IOException ex) {
            Logger.getLogger(RomManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void parseOffsets(int romType){
        System.out.println("com.lsc.ls.text.io.RomManager.parseOffsets() - Parsing offsets ...");
        byte[] data = Arrays.copyOfRange(romData,HUFFMANTREEOFFSETS_OFFSETS[romType][0],HUFFMANTREEOFFSETS_OFFSETS[romType][1]);
        TextDecoder.parseOffsets(data);
        System.out.println("com.lsc.ls.text.io.RomManager.parseOffsets() - Offsets parsed.");
    }
    
    private static void parseTrees(int romType){
        System.out.println("com.lsc.ls.text.io.RomManager.parseTrees() - Parsing trees ...");
        byte[] data = Arrays.copyOfRange(romData,HUFFMANTREES_OFFSETS[romType][0],HUFFMANTREES_OFFSETS[romType][1]);
        TextDecoder.parseTrees(data);
        System.out.println("com.lsc.ls.text.io.RomManager.parseTrees() - Trees parsed.");
    }
    
    private static String[] parseAllTextbanks(int romType, int lastLineIndex){
        System.out.println("com.lsc.ls.text.io.RomManager.parseTextbank() - Parsing textbank ...");
        String[] textlines = new String[0];     
        int numberOfTextbanks = (lastLineIndex+1 + LINES_PER_BANK-1) / LINES_PER_BANK;
        int lastTextbankLines = ((lastLineIndex+1) % LINES_PER_BANK);
        for(int i=0;i<9;i++){
            int linesToParse = (i==numberOfTextbanks-1)? lastTextbankLines : LINES_PER_BANK;
            byte[] data = Arrays.copyOfRange(romData,TEXTBANKS_OFFSETS[romType][i],TEXTBANKS_OFFSETS[romType][i+1]); 
            String[] textbankStrings = TextDecoder.parseTextbank(data, i, linesToParse);
            String[] workingStringArray = Arrays.copyOf(textlines, textlines.length + textbankStrings.length);
            System.arraycopy(textbankStrings, 0, workingStringArray, textlines.length, textbankStrings.length);
            textlines = workingStringArray;
        }
        System.out.println("com.lsc.ls.text.io.RomManager.parseTextbank() - Textbanks all parsed.");
        return textlines;
    }
    
    private static void produceTrees(String[] textlines) {
        System.out.println("com.lsc.ls.text.io.RomManager.produceTrees() - Producing trees ...");
        TextEncoder.produceTrees(textlines);
        System.out.println("com.lsc.ls.text.io.RomManager.produceTrees() - Trees produced.");
    }

    private static void produceTextbanks(String[] textlines) {
        System.out.println("com.lsc.ls.text.io.RomManager.produceTextbanks() - Producing text banks ...");
        TextEncoder.produceTextbanks(textlines);
        System.out.println("com.lsc.ls.text.io.RomManager.produceTextbanks() - Text banks produced.");
    }    
  
    private static void writeFile(int romType, String romFilePath){
        try {
            System.out.println("com.lsc.ls.text.io.RomManager.writeFiles() - Writing file ...");
            boolean override = false;
            romFile = new File(romFilePath);
            Path romPath = Paths.get(romFile.getAbsolutePath());
            romData = Files.readAllBytes(romPath);
            byte[] newHuffmantreeOffsetsFileBytes = TextEncoder.getNewHuffmantreeOffsetsFileBytes();
            System.arraycopy(newHuffmantreeOffsetsFileBytes, 0, romData, HUFFMANTREEOFFSETS_OFFSETS[romType][0], newHuffmantreeOffsetsFileBytes.length);
            System.out.println("Huffman tree offsets : "+newHuffmantreeOffsetsFileBytes.length 
                    + " bytes written at offset 0x" + Integer.toHexString(HUFFMANTREEOFFSETS_OFFSETS[romType][0]).toUpperCase()); 
            int originalHuffmantreeOffsetsSize = HUFFMANTREEOFFSETS_OFFSETS[romType][1]-HUFFMANTREEOFFSETS_OFFSETS[romType][0];
            if(newHuffmantreeOffsetsFileBytes.length>originalHuffmantreeOffsetsSize){
                override = true;
                System.err.println("ERROR : NEW HUFFMAN TREE OFFSETS SIZE "+newHuffmantreeOffsetsFileBytes.length
                        +" IS HIGHER THAN ORIGINAL SIZE "+originalHuffmantreeOffsetsSize+" ! THIS WILL OVERRIDE NEXT DATA CHUNK.");
            }
            byte[] newHuffmanTreesFileBytes = TextEncoder.getNewHuffmanTreesFileBytes();
            System.arraycopy(newHuffmanTreesFileBytes, 0, romData, HUFFMANTREES_OFFSETS[romType][0], newHuffmanTreesFileBytes.length);
            System.out.println("Huffman trees : "+newHuffmanTreesFileBytes.length 
                    + " bytes written at offset 0x" + Integer.toHexString(HUFFMANTREES_OFFSETS[romType][0]).toUpperCase());           
            int originalHuffmantreesSize = HUFFMANTREES_OFFSETS[romType][1]-HUFFMANTREES_OFFSETS[romType][0];
            if(romType==ORIGINAL_ROM_TYPE && newHuffmanTreesFileBytes.length>originalHuffmantreesSize){
                override = true;
                System.err.println("ERROR : NEW HUFFMAN TREES SIZE "+newHuffmanTreesFileBytes.length
                        +" IS HIGHER THAN ORIGINAL SIZE "+originalHuffmantreesSize+" ! THIS WILL OVERRIDE TEXTBANK 00."
                        + "\nTo avoid this, use a caravan-expanded rom, or use a disassembly.");
            }            
            byte[][] newTextbanks = TextEncoder.getNewTextbanks();
            for(int i=0;i<newTextbanks.length;i++){
                String index = String.valueOf(i);
                System.arraycopy(newTextbanks[i], 0, romData, TEXTBANKS_OFFSETS[romType][i], newTextbanks[i].length);
                int sizeComparison = (TEXTBANKS_OFFSETS[romType][i+1]-TEXTBANKS_OFFSETS[romType][i]) - newTextbanks[i].length;
                StringBuilder sb = new StringBuilder();
                if(sizeComparison > 0){
                    sb.append("Saved ").append(sizeComparison).append(" bytes compared to original size (").append(TEXTBANKS_OFFSETS[romType][i+1]-TEXTBANKS_OFFSETS[romType][i]).append(" bytes)");
                }else{
                    sb.append("Added ").append(sizeComparison).append(" bytes compared to original size (").append(TEXTBANKS_OFFSETS[romType][i+1]-TEXTBANKS_OFFSETS[romType][i]).append(" bytes)");
                }
                System.out.println("Textbank "+index+" : "+newTextbanks[i].length 
                        + " bytes written at offset 0x" + Integer.toHexString(TEXTBANKS_OFFSETS[romType][i]).toUpperCase()
                        + " <- " + sb.toString());
                int originalTextbankSize = TEXTBANKS_OFFSETS[romType][i+1]-TEXTBANKS_OFFSETS[romType][i];
                if(newTextbanks[i].length>originalTextbankSize){
                    override = true;
                    System.err.println("ERROR : NEW TEXTBANK SIZE "+newTextbanks[i].length
                            +" IS HIGHER THAN ORIGINAL SIZE "+originalTextbankSize+" ! THIS WILL OVERRIDE NEXT DATA ITEM."
                            + "\nTo avoid this, use a disassembly.");
                }                 
            }
            Files.write(romPath,romData);
            System.out.println(romData.length + " bytes into " + romFilePath);  
            if(override){
                System.err.println("ERROR : DATA OVERRIDING HAPPENED. MORE DETAILS IN PREVIOUS LOGS.");
            }
            System.out.println("com.lsc.ls.text.io.RomManager.writeFiles() - File written.");
        } catch (IOException ex) {
            Logger.getLogger(TextManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
