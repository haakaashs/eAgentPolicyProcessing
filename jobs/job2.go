package main

import (
	"fmt"
	"os"
)

func main() {
	// Directory path
	directoryPath := "./files/"

	// File names
	FileName := "D49835.T45245.control.xml"

	// Create the directory if it doesn't exist
	if err := os.MkdirAll(directoryPath, os.ModePerm); err != nil {
		fmt.Println("Error creating directory:", err)
		return
	}

	// Create XML file
	FilePath := directoryPath + FileName
	_, err := os.Create(FilePath)
	if err == nil {
		fmt.Println("control file created:", FilePath)
	} else if os.IsExist(err) {
		fmt.Println("control file already exists.")
	} else {
		fmt.Println("Error creating control file:", err)
	}
}
