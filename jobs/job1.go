package main

import (
	"fmt"
	"os"
)

func main() {
	// Directory path
	directoryPath := "./files"

	// File names
	xmlFileName := "D111.T123.T111.xml"
	pdfFileName := "D111.T123.T111.pdf"

	// Create the directory if it doesn't exist
	err := os.MkdirAll(directoryPath, os.ModePerm)
	if err != nil {
		fmt.Println("Error creating directory:", err)
		return
	}
	createFile(directoryPath, xmlFileName, "XML")
	createFile(directoryPath, pdfFileName, "PDF")
}

func createFile(directoryPath, fileName, fileType string) {
	// Create XML file
	file, err := os.Create(directoryPath + fileName)
	if err == nil {
		defer file.Close()
		fmt.Println(fileType+" file created:", file.Name())
	} else {
		fmt.Println("Error creating "+fileType+" file:", err)
	}
}
