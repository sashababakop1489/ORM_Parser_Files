# ORM_Parser_Files


    Add your file to the resource folder. It can be (json, xml, csv) format

    You should also change the name of fields in the model class

For example it's my model

After all the above actions let's go to the Parser class and change "process" method It should have

File fileFormat = new File("src/main/resources/format.");
DataReadWriteSource<?> source = new FileReadWriteSource(fileFormat);
result = ORM.readAll(source, Model.class);

Comment: You must change Person class is your model class

If you want parse 'json' format

File json = new File("src/main/resources/format.json");
DataReadWriteSource<?> csvFile = new FileReadWriteSource(json);

If you want parse 'csv' format

File csv = new File("src/main/resources/format.csv");
DataReadWriteSource<?> csvFile = new FileReadWriteSource(csv);

If you want parse 'xml' format

File xml = new File("src/main/resources/format.xml");
DataReadWriteSource<?> csvFile = new FileReadWriteSource(xml);

    If you want see the result in console write this in "process" method

  for (Person person : result) {
    System.out.println(person);
  }

Comment: Where Person is your model class
