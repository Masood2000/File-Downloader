package com.masood

import FileMetadata
import SimpleDownloader
import java.io.File
import java.util.Date

fun main() {


    val url = "http://localhost:8080/abc.txt"

    val date = Date()
    val time = date.toLocaleString().replace(" ","-").replace(",","").replace(":","-")

    val outputFile = File("D:/Learning/jetbrains/Integrating reporting SDK into web frontends/server/downloads/")
    val outputFilePath = outputFile.absolutePath  +"/downloaded-file-" + time +".txt";


    val fileMetadata = FileMetadata();
    val simpleDownloader = SimpleDownloader();

    fileMetadata.getMetaData(url);


    simpleDownloader.downloadFile(url, outputFilePath)



}