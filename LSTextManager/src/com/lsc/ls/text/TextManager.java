/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsc.ls.text;

import com.lsc.ls.text.io.AsmManager;
import com.lsc.ls.text.io.CustomManager;
import com.lsc.ls.text.io.TxtManager;
import com.lsc.ls.text.io.DisassemblyManager;
import com.lsc.ls.text.io.RomManager;

/**
 *
 * @author wiz
 */
public class TextManager {
    
    public static String[] gamescript;
       
    public static void importDisassembly(String basePath){
        System.out.println("com.lsc.ls.text.TextManager.importDisassembly() - Importing disassembly ...");
        TextManager.gamescript = DisassemblyManager.importDisassembly(basePath);
        System.out.println("com.lsc.ls.text.TextManager.importDisassembly() - Disassembly imported.");
    }
    
    public static void exportDisassembly(String basePath){
        System.out.println("com.lsc.ls.text.TextManager.importDisassembly() - Exporting disassembly ...");
        DisassemblyManager.exportDisassembly(gamescript, basePath);
        System.out.println("com.lsc.ls.text.TextManager.importDisassembly() - Disassembly exported.");        
    }   
    
    public static void importOriginalRom(String originalRomFilePath, int lastLineIndex){
        System.out.println("com.lsc.ls.text.TextManager.importOriginalRom() - Importing original ROM ...");
        TextManager.gamescript = RomManager.importRom(RomManager.ORIGINAL_ROM_TYPE,originalRomFilePath, lastLineIndex);
        System.out.println("com.lsc.ls.text.TextManager.importOriginalRom() - Original ROM imported.");
    }
    
    public static void exportOriginalRom(String originalRomFilePath){
        System.out.println("com.lsc.ls.text.TextManager.exportOriginalRom() - Exporting original ROM ...");
        RomManager.exportRom(RomManager.ORIGINAL_ROM_TYPE, TextManager.gamescript, originalRomFilePath);
        System.out.println("com.lsc.ls.text.TextManager.exportOriginalRom() - Original ROM exported.");        
    }   
    
    public static void importAlternateRom(String caravanRomFilePath, int lastLineIndex){
        System.out.println("com.lsc.ls.text.TextManager.importCaravanRom() - Importing Caravan ROM ...");
        TextManager.gamescript = RomManager.importRom(RomManager.ALTERNATE_ROM_TYPE,caravanRomFilePath, lastLineIndex);
        System.out.println("com.lsc.ls.text.TextManager.importCaravanRom() - Caravan ROM imported.");
    }
    
    public static void exportCaravanRom(String caravanRomFilePath){
        System.out.println("com.lsc.ls.text.TextManager.exportCaravanRom() - Exporting Caravan ROM ...");
        RomManager.exportRom(RomManager.ALTERNATE_ROM_TYPE, TextManager.gamescript, caravanRomFilePath);
        System.out.println("com.lsc.ls.text.TextManager.exportCaravanRom() - Caravan ROM exported.");        
    } 
    
    public static void importCustomRom(String customRomFilePath, int huffmanTreeOffsetsBegin, int huffmanTreeOffsetsEnd, int huffmanTreesOffsetsBegin, int huffmanTreesOffsetsEnd, int textbanksOffsetsPointerOffset, int lastLineIndex){
        System.out.println("com.lsc.ls.text.TextManager.importCustomRom() - Importing Custom ROM ...");
        TextManager.gamescript = CustomManager.importRom(customRomFilePath, huffmanTreeOffsetsBegin, huffmanTreeOffsetsEnd, huffmanTreesOffsetsBegin, huffmanTreesOffsetsEnd, textbanksOffsetsPointerOffset, lastLineIndex);
        System.out.println("com.lsc.ls.text.TextManager.importCustomRom() - Custom ROM imported.");
    }
    
    public static void exportCustomRom(String customRomFilePath, int huffmanTreeOffsetsOffset, int huffmanTreesOffset, int textbanksPointerOffset, int textbanksOffset){
        System.out.println("com.lsc.ls.text.TextManager.exportCustomRom() - Exporting Custom ROM ...");
        CustomManager.exportRom(TextManager.gamescript, customRomFilePath, huffmanTreeOffsetsOffset, huffmanTreesOffset, textbanksPointerOffset, textbanksOffset);
        System.out.println("com.lsc.ls.text.TextManager.exportCustomRom() - Custom ROM exported.");        
    }     
    
    public static void importTxt(String filepath){
        System.out.println("com.lsc.ls.text.TextManager.importTxt() - Importing TXT ...");
        gamescript = TxtManager.importTxt(filepath);
        System.out.println("com.lsc.ls.text.TextManager.importTxt() - TXT imported.");
    }
    
    public static void exportTxt(String filepath){
        System.out.println("com.lsc.ls.text.TextManager.exportTxt() - Exporting TXT ...");
        TxtManager.exportTxt(gamescript, filepath);
        System.out.println("com.lsc.ls.text.TextManager.exportTxt() - TXT exported.");       
    }
       
    public static void importAsm(String path){
        System.out.println("com.lsc.ls.text.TextManager.importDisassembly() - Importing disassembly ...");
        TextManager.gamescript = AsmManager.importAsm(path, TextManager.gamescript);
        System.out.println("com.lsc.ls.text.TextManager.importDisassembly() - Disassembly imported.");
    }
     
    public static String getLine(int index){
        return gamescript[index];
    }
    
    public static void setLine(int index, String line){
        gamescript[index] = line;
    }
    
    public static int addLine(String line){
        String[] newGamescript = new String[gamescript.length+1];
        System.arraycopy(gamescript, 0, newGamescript, 0, gamescript.length);
        newGamescript[gamescript.length] = line;
        gamescript = newGamescript;
        return gamescript.length-1;
    }
    
}
