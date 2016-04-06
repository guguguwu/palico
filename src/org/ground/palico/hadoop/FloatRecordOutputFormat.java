package org.ground.palico.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.IOException;

public class FloatRecordOutputFormat extends FileOutputFormat<LongWritable, FloatWritable> {
    @Override
    public RecordWriter<LongWritable, FloatWritable>
    getRecordWriter(TaskAttemptContext job) throws IOException, InterruptedException {
        Configuration conf = job.getConfiguration();
        boolean isCompressed = getCompressOutput(job);

        CompressionCodec codec = null;
        String extension = "";

        if (isCompressed) {
            Class<? extends CompressionCodec> codecClass = getOutputCompressorClass(job, GzipCodec.class);
            codec = ReflectionUtils.newInstance(codecClass, conf);
            extension = codec.getDefaultExtension();
        }
        Path file = getDefaultWorkFile(job, extension);
        FileSystem fs = file.getFileSystem(conf);

        if (!isCompressed) {
            FSDataOutputStream fileOut = fs.create(file, false);
            return new FloatRecordWriter(fileOut);
        } else {
            FSDataOutputStream fileOut = fs.create(file, false);
            return new FloatRecordWriter
                    (new FSDataOutputStream(codec.createOutputStream(fileOut)));
        }
    }
}