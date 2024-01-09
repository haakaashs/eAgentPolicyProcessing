package main

import (
	"fmt"
	"os"
)

func main() {
	// Directory path
	directoryPath := "./completed/"

	// File names
	fileName := "final.txt"

	// Create the directory if it doesn't exist
	err := os.MkdirAll(directoryPath, os.ModePerm)
	if err != nil {
		fmt.Println("Error creating directory:", err)
		return
	}

	file, err := os.Create(directoryPath + fileName)
	if err == nil {
		defer file.Close()
		fmt.Println("final file created successfully")
	} else {
		fmt.Println("Error creating final file:", err)
	}
}
