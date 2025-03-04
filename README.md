# RB 4 Flo Energy Take-home Exercise

Welcome to my submission, I hope you enjoy reading it.

## Tools and environment
* Built on Apple Mac, IntelliJ, and various other tools
* Developed and run with Eclipse Temurin 21 JDK
* Gradle Multi-project
* Kotlin 2.1.10
* Extra tools
  * My bash script to build NEM12 CSV data using predicatable pattern: `tools/createNem12.sh`
  * An online Postgres Playground for testing the create table and inserts: `https://aiven.io/tools/pg-playground`

## Run
Classic Gradle make project: `./gradelw clean build`

## Assumptions - critical section

* Assuming a Postgres create table was given.
* The project is designed to create the insert statements from very large datasets. I does not run a database and insert the data at all.
* Inserts are generated into one output text file. This file is must larger than the CSV input.
* I wanted to 'store to the database' all the consumption values from each NMI. I think this would be meaningful for the business and also possibly required. So I create an insert for each intervalValue from each 300 record.
  * An alternative was to aggregate the consumption for the day and store that - but that's reducing our business data quality (though saving storage).
  * Also, your documentation said that `IntervalLength` was of interest. If I created an aggregate of the consumption then I'd not have needed this value. Highlighting `IntervalLength` led me in the direction of deriving the timestamp of each interval to be able to create an insert for it.

## Testing

### Things I tested by hand

* Creating table using an online Postgres playground
* Inserting my generated insert statements into above tool and checking rowcounts.
* Checking memory usage of nem12-csv-processor using `htop` to see its RAM usage - stable over time, independent of input and output size.
* Tested using a 500 MB csv file and my tool generated 9 GB of output.

You'll see that I built a bash script tool to generate NEM12 CSV files. I used this extensively to test various scenarios including large datasets. It can also generate slightly inappropriate data like using an invalid IntervalLength when requested.

## Write-up Answers

### Advantages of technologies used
* Modern and open toolchain.
* Kotlin on JVM is incredibly typesafe whilst more modern than prior Java; type inference makes reading and writing a dream.
* Kotlin on JDK offer a rich functional and OO world to build mixed applications. Java offers rich IO like buffered writing, whilst Kotlin offers, IMHO, incredibly good collections library with excellent FP support.
* All tools used have excellent industry and community support.
* Why JVM? Widely used, fast and robust multiplatform virtual machine.

### How is code designed and structured?
* The solution to the problem of generating inserts from NEM12 files is a `unix tool` -type of program: run from bash, provide a file as input and expect a file as output.
* This is designed to run as a batch processing job. Success gives zero exit code; failure is non-zero.
* Designed to fail-fast with basic reporting of error location - like CSV line number.
* NEM12 is possibly received/downloaded/offered to Flo in one of many ways. I don't know the exact way, so I designed most of my code to be reusable should a filesystem CSV file not be available or possible.
  * For example: most of the code can be reused if one were to read data from an AWS S3 object and writing to some other store directly.
* Structure: three Gradle projects make up my solution:
  * Domain items capturing NEM concepts.
  * A line-at-a-time lazy processing facility for the NEM12 CSV lines, writing through a `Consumer` concept to abstract actual writing out of the NEM handling.
  * A filesystem CSV processor which uses the above, drawing data from a `file` and writing to a `file`.
* The multi-project abstractions cost nothing to use at runtime yet offer clean separation of concerns as well as deliberately creating freedom-of-implementation points. Like the Consumer for writing, and the Sequence for lazily taking the input.

### How does the design help to make the codebase readable and maintainable for other engineers?
* Separation of concerns; projects group code by concern.
* Uses some SOLID principles like Liskov substition applies to the Sequence and Consumer IO. Single Responsibility applies at the project level.
* Mostly pure functions. State is entirely contained to the InsertStreamGenerator.

## Discuss design patterns ...
* Somewhat discussed above, but also:
* Lazy read and abstracted writing, as well deliberately not holding NEM data other than one 200 record are my design to keep the memory footprint small and constant regardless of the input and output side.
* I quite like my LineNumberException detail, which assists a developer or user (which would be a batch job) should an error occur while in use.
* I make use of Kotlin extension methods, raw functions (as opposed to Java where everything is in a class) and higher-order functions (like `withLineNumberException`).
* I make use of Kotlin Scope Functions in prod and test code, they help to keep the number of scoped vals down.
* `val` unless I need `var`.
* Immutable data first.
* Functional Programming first, except where it's "against the grain" and is harder work than stateful imperative.

## What would you do better next time?
I'm quite happy with my solution shape, given the small input requirements.

While I don't think my solution is slow, it can take some time to produce a lot of output. In one test, for a 500 MB input CSV file, about 9 GB of insert statements were produced. This might take too long, and so maybe parallel processing might be required. Given the line dependencies inside a NEM12 file, this could be achieved maybe using a clever partitioning approach. The processing those in parallel. However - this work is heavily IO-bound and so I don't expect much speedup from multi-core parallelisation.

Of course for a production solution, much more guarding and testing should be done. That is to say my prod code does not cover every eventuality, nor do my tests.

A feature that would be nice to add would be basic stats output from CSV processing. However care is needed, for example "count discrete NMI" could end up with a very large `HashMap`. That would incur holding in memory millions of NMIs which could be not only a memory issue but also an in-bucket search lookup issue within the HashMap.

## Reflect on areas where you see room for improvement and describe how you would approach them differently in future projects.

I think i've discussed this above.

Primarily considerably better test coverage and also prod code coverage of more data constraints.

## What other ways could you have done this project?
## Explore alternative approaches or technologies that you considered during the development of the project.

* Given Kotlin is in the job description that i'm applying for, I did not consider any other language or platform than the one which I am very confident with and that I know will achieve a good outcome with me.
* Spring Batch could be utilised but it's unnecessary for this task.
* Consideration needs to be given to the actual deploy site - how/where would this tool really run. Because it requires files for IO.
* The CSV lines could be stored in a database rather than a file, and the insert generator could read its input from the db. Would also allow for some parallel processing as the data would no longer be in linear access. Batch processor instances could be used to process each 200+300s records at a time.
* At no point did I feel the need to use an ORM. Of course if we had common schema in use then that could be re-used here.

Serious consideration needed to complete this topic:

* The insert statements are free-standing - they are not accompanied by and loading program. Such a 'loader' would need to use a transaction so that in the presence of problems we can atomically roll back, and atomically commit if successful.
* I would probably also assign all the inserted records to a 'batch' which included the load date, the filename being loaded.
* The data size is so massive that we need processes and tools to manage these records at scale. If we need to delete a batch and reload it all (imagine there's a bug in the csv processor!) then we need to be able to clean up and re-process.


I hope you enjoy my solution, look forward to your feedback.
