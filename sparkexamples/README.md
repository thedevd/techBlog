## RDDs
1. Read a file and count words - [WordCount.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/rdd/WordCount.scala)
2. Sum of prime numbers problem - [SumOfPrimeNumbersFromFile.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/rdd/SumOfPrimeNumbersFromFile.scala)
3. Zip Transformation on RDD - [RddZipOperation.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/rdd/RddZipOperation.scala)
4. ZipWithIndex Transformation on RDD - [RddZipWithIndex.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/rdd/RddZipWithIndex.scala)
5. Union, Sample (Transformation) and takeSample (Action) - [RddSampleOperation.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/rdd/RddSampleOperation.scala)
6. Airport located in USA problem - [AirportProblem.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/rdd/AirportProblem.scala)
7. SameHostAccessed on both days - [SameHostsAccessedProblem.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/rdd/SameHostsAccessedProblem.scala)
8. **fold vs reduce** action - [FoldVsReduce.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/differences/FoldVsReduce.scala)

## PairRDD
1. AvgHousePriceProblem Using **FoldByKey** - [AvgHousePriceProblemUsingFoldByKey.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/aggregates/AvgHousePriceProblemUsingFoldByKey.scala)
2. AvgHousePriceProblem Using **ReduceByKey** - [AvgHousePriceProblemUsingReduceByKey.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/aggregates/AvgHousePriceProblemUsingReduceByKey.scala)
3. AvgHousePriceProblem Using **AggregateByKey** - [AvgHousePriceProblemUsingAggregateByKey.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/aggregates/AvgHousePriceProblemUsingAggregateByKey.scala)  
4. AvgHousePriceProblem Using **CombineByKey** - [AvgHousePriceProblemUsingCombineByKey.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/aggregates/AvgHousePriceProblemUsingCombineByKey.scala) 
5. Student Max Subject Scoring Problem using **AggregateByKey** - [StudentScoreProblem.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/aggregates/StudentScoreProblem.scala)
6. Airport List By Contry Problem Using **GroupByKey** and **reduceByKey** - [AirportContryProblemUsingGroupByKey.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/groupByKey/AirportContryProblemUsingGroupByKey.scala)
7. **GroupByKeyVsReduceByKey** to solve WordCount problem - [GroupByKeyVsReduceByKey.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/groupByKey/GroupByKeyVsReduceByKey.scala)
8. [SubtractByKey.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/SubtractByKey.scala)
9. **Joins** on pairRDD - [InnerJoin](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/join/InnerJoinOnRdd.scala), [RightOuterJoin](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/join/RightOuterJoinOnRdd.scala)
10. WordCountProblem **sortBy** asc order of count - [WordCountProblemSortedByCount.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/pairRdd/sort/WordCountProblemSortedByCount.scala)
11. **reduce vs reduceByKey** - [ReduceVsReduceByKey.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/differences/ReduceVsReduceByKey.scala)

## SparkSQL (DataFrames and DataSets)
1. **RDD vs DataFrames vs Dataset** - [RddVsDataFrameVsDataSet.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/RddVsDataFrameVsDataSet.scala)
2. **DataFrame Vs DataSet** - [DataFrameVsDataSet.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/DataFrameVsDataSet.scala)
3. **Rdd To DataFrame Conversion** - [RddToDataFrameConversion.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/RddToDataFrameConversion.scala)
4. AvgHousePriceProblem using sparkSQL - [HouseAvgPriceProblem.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/HouseAvgPriceProblem.scala)
5. Adding Column To DataFrame - [AddingColumnToDataFrame.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/AddingColumnToDataFrame.scala)
6. **Spark UDF (User defined functions)** - [SparkUDF.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/SparkUDF.scala)
7. Student's max and min scoring subject problem - [SparkSqlAggregation.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/SparkSqlAggregation.scala)
8. Reading Complex CSV as DataFrame - [ReadComplexCSVInDataFrame.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/ReadComplexCSVInDataFrame.scala)
9. Generating Row Sequence Numbers using ZipWithIndex - [SequenceNumberInDataFrameUsingZipWithIndex.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/SequenceNumberInDataFrameUsingZipWithIndex.scala)
10. **Filter vs Where** - [FilterVsWhereInSparkSql.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/differences/FilterVsWhereInSparkSql.scala)

## SparkSQL Joins
1. **Inner Join** - [InnerJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/joins/InnerJoin.scala)
2. **Self Join** - [SelfJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/joins/SelfJoin.scala)
3. **Left Join** - [LeftJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/joins/LeftJoin.scala)
4. **Right Join** - [RightJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/joins/RightJoin.scala)
5. **Full Outer Join** - [FullOuterJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/joins/FullOuterJoin.scala)
6. **LeftSemi Join** - [LeftSemiJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/joins/LeftSemiJoin.scala)
7. **LeftAnti Join** - [LeftAntiJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sparksql/joins/LeftAntiJoin.scala)

## SparkSQL reading/writing fileFormats (Avro, Parquet, JSON, CSVs)
First read this - [File Formats in Hadoop Ecosystem](https://github.com/thedevd/techBlog/wiki/File-formats-in-Hadoop-ecosystem)
1. JSON format - [JsonRead.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/fileformats/JsonRead.scala)
2. Reading multiple CSV files - [MultipleCsvFileLoadInDF.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/fileformats/MultipleCsvFileLoadInDF.scala)
3. Avro read/write - [AvroReadWrite.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/fileformats/AvroReadWrite.scala)
4. Parquet read/write - [ParquetReadWrite.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/fileformats/ParquetReadWrite.scala)

## Shared Variables (broadcast and accumulators)
1. Broadcast Variables - [BroadcastVariable.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sharedvariables/BroadcastVariable.scala), [BroadcastVariableWithUDF.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sharedvariables/BroadcastVariableWithUDF.scala)
2. Accumulators - [Accumulator.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sharedvariables/Accumulator.scala)
3. Creating Custom Accumulators - [CustomAccumulator.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sharedvariables/CustomAccumulator.scala)
4. Black listed wordCount problem using custom accumulator - [BlacklistWordCount.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/sharedvariables/BlacklistWordCount.scala)

## Partitioning Concepts in Spark
1. **spark.default.parallelism** (default no of partitions) - [SparkDefaultParallelism.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/partitions/SparkDefaultParallelism.scala)
2. **Hash Partitioner and Range Partitioner** - [HashAndRangePartitioners.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/partitions/HashAndRangePartitioners.scala)
3. **spark.sql.shuffle.partitions** - [SparkSqlShufflePartitions.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/partitions/SparkSqlShufflePartitions.scala)
4. **Coalesce Vs repartition** (Partition management operations) - [CoalesceVsRepartition.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/differences/CoalesceVsRepartition.scala)
5. **map() vs mapPartitions() transformation** - [MapVsMapPartitions.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/differences/MapVsMapPartitions.scala)
6.  **mapPartitions() vs mapPartitionsWithIndex() transformation** - [MapPartitionsVsMapPartitionsWithIndex.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/differences/MapPartitionsVsMapPartitionsWithIndex.scala)

## Spark Structured streaming
1. **Structured streaming using File Source** - [SparkStructuredStreamingWithFileSource.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/streaming/filestreams/SparkStructuredStreamingWithFileSource.scala)
2. **Structured streaming using Kafka Soruce** - [SparkStructuredStreamingWithKafkaSource.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/streaming/kafkasource/SparkStructuredStreamingWithKafkaSource.scala)
3. **Output Modes** for sink
   * Complete Output mode - [CompleteOutputModeDemo.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/streaming/kafkasource/CompleteOutputModeDemo.scala)
   * Update Output mode - [UpdateOutpuModeDemo.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/streaming/kafkasource/UpdateOutpuModeDemo.scala)
   * Append Output mode - [Append Mode](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/streaming/filestreams/SparkStructuredStreamingWithFileSource.scala)
4. **Window and watermark based structured streaming** - [CarOverSpeeding UseCase](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/streaming/windowing/CarOverSpeedingAggregation.scala)

## Spark Optimizations
1. **Predicate Push Down** - [PredicatePushDown.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/optimizations/PredicatePushDown.scala)
2. **Bucketing in Spark using bucketBy()** - [SparkBucketing.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/optimizations/SparkBucketing.scala)
3. **Algorithm used by SparkSQL engine to join dataframe**
   * BroadcastHashJoin - [BroadcastHashJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/optimizations/BroadcastHashJoin.scala)
   * ShuffleHashJoin - [ShuffleHashJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/optimizations/ShuffleHashJoin.scala)
   * SortMergeJoin - [SortMergeJoin.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/optimizations/SortMergeJoin.scala)
4. **Cost Based Optimizer (CBO)** - 
   * CBO on join query - [CostBasedOptimizerWithJoinQuery.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/optimizations/CostBasedOptimizerWithJoinQuery.scala)
   * CBO on join with filter criteria - [CostBasedOptimizerWithJoinAndFilterQuery.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/optimizations/CostBasedOptimizerWithJoinAndFilterQuery.scala) 
   
## Spark Integration Connector 
1. Spark-Cassandra Integration using Spark-Cassandra-Connector - [SparkCassandraConnector.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/integration/SparkCassandraConnector.scala)
2. Spark-JDBC integration (for mysql) - [SparkJDBCIntegration.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/integration/SparkJDBCIntegration.scala)

## Special scenarios of Spark framework
1. **Multiple Spark Contexts** - [MultipleSparkContext.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/specialcase/MultipleSparkContext.scala)
2. **Multiple SparkSessions** - [MultipleSparkSession.scala](https://github.com/thedevd/techBlog/blob/master/sparkexamples/src/main/scala/com/thedevd/sparkexamples/specialcase/MultipleSparkSession.scala)
