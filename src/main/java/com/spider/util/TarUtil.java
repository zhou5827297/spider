package com.spider.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 */
public class TarUtil {


    /**
     * tar 打包压缩 tar.gz文件
     *
     * @param source 源文件
     * @param dest   目标文件
     */
    public static void tarGz(File source, File dest) {
        tar(source, dest);
        gzip(dest);
    }


    /**
     * tar 打包
     *
     * @param source 源文件
     * @param dest   目标文件
     */
    public static void tar(File source, File dest) {
        FileOutputStream out = null;
        TarArchiveOutputStream tarOut = null;

        try {
            out = new FileOutputStream(dest);
            tarOut = new TarArchiveOutputStream(out);
            //解决文件名过长
            tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
            tarPack(source, tarOut, "");
            tarOut.flush();
            tarOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (tarOut != null) {
                    tarOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 归档
     *
     * @param source     源文件或者目录
     * @param tarOut     归档流
     * @param parentPath 归档后的目录或者文件路径
     */
    public static void tarPack(File source, TarArchiveOutputStream tarOut, String parentPath) {
        if (source.isDirectory()) {
            tarDir(source, tarOut, parentPath);
        } else if (source.isFile()) {
            tarFile(source, tarOut, parentPath);
        }
    }

    /**
     * 归档文件(非目录)
     *
     * @param source     源文件
     * @param tarOut     归档流
     * @param parentPath 归档后的路径
     */
    public static void tarFile(File source, TarArchiveOutputStream tarOut, String parentPath) {
        TarArchiveEntry entry = new TarArchiveEntry(parentPath + source.getName());
        BufferedInputStream bis = null;
        FileInputStream fis = null;
        try {
            entry.setSize(source.length());
            tarOut.putArchiveEntry(entry);
            fis = new FileInputStream(source);
            bis = new BufferedInputStream(fis);
            int count = -1;
            byte[] buffer = new byte[1024];
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                tarOut.write(buffer, 0, count);
            }
            bis.close();
            tarOut.closeArchiveEntry();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 归档目录
     *
     * @param sourceDir  原目录
     * @param tarOut     归档流
     * @param parentPath 归档后的父目录
     */
    public static void tarDir(File sourceDir, TarArchiveOutputStream tarOut, String parentPath) {
        //归档空目录
        if (sourceDir.listFiles().length < 1) {
            TarArchiveEntry entry = new TarArchiveEntry(parentPath + sourceDir.getName() + "\\");
            try {
                tarOut.putArchiveEntry(entry);
                tarOut.closeArchiveEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //递归 归档
        for (File file : sourceDir.listFiles()) {
            tarPack(file, tarOut, parentPath + sourceDir.getName() + "\\");
        }
    }

    /**
     * 解压tar.gz文件
     *
     * @param source
     * @param dest
     */
    public static void unTar(File source, File dest) {
        InputStream is = null;
        CompressorInputStream in = null;
        TarArchiveInputStream tin = null;
        OutputStream out = null;
        try {
            is = new FileInputStream(source);
            in = new GzipCompressorInputStream(is, true);
            tin = new TarArchiveInputStream(in);
            TarArchiveEntry entry = tin.getNextTarEntry();
            while (entry != null) {
                File archiveEntry = new File(dest, entry.getName());
                archiveEntry.getParentFile().mkdirs();
                if (entry.isDirectory()) {
                    archiveEntry.mkdir();
                    entry = tin.getNextTarEntry();
                    continue;
                }
                out = new FileOutputStream(archiveEntry);
                IOUtils.copy(tin, out);
                out.close();
                entry = tin.getNextTarEntry();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (tin != null) {
                    tin.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws Exception {
//        TarUtil.tarGz(new File("C:\\Users\\zhoukai\\Desktop\\spider\\SiteManage\\data\\127.0.0.1"), new File("C:\\Users\\zhoukai\\Desktop\\spider\\SiteManage\\data\\127.0.0.1.tar"));
        TarUtil.unTarGz("data\\sites.tar.gz", "sites");
    }

    /**
     * gzip 压缩，跟源文件在相同目录中生成.gz文件
     *
     * @param source 源文件
     */
    public static void gzip(File source) {
        String sourcePath = source.getAbsolutePath();
        int lastIndexOf = sourcePath.lastIndexOf("\\");
        String dir = sourcePath.substring(0, lastIndexOf);
        File target = new File(dir + "\\" + source.getName() + ".gz");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        GZIPOutputStream gzipOS = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(target);
            gzipOS = new GZIPOutputStream(fos);
            int count = -1;
            byte[] buffer = new byte[1024];
            while ((count = fis.read(buffer, 0, buffer.length)) != -1) {
                gzipOS.write(buffer, 0, count);
            }
            gzipOS.flush();
            gzipOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (gzipOS != null) {
                    gzipOS.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 数据解压缩
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decompress(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 解压缩

        decompress(bais, baos);

        data = baos.toByteArray();

        baos.flush();
        baos.close();

        bais.close();

        return data;
    }

    /**
     * 文件解压缩
     *
     * @param file
     * @throws Exception
     */
    public static void decompress(File file) throws Exception {
        decompress(file, true);
    }

    /**
     * 文件解压缩
     *
     * @param file
     * @param delete 是否删除原始文件
     */
    public static void decompress(File file, boolean delete) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(file.getPath().replace(".gz", ""));
            decompress(fis, fos);
            fis.close();
            fos.flush();
            fos.close();

            if (delete) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件解压缩
     *
     * @throws Exception
     */
    public static void decompress(String inputFileName, String outputFileName)
            throws Exception {
        FileInputStream inputFile = new FileInputStream(inputFileName);
        FileOutputStream outputFile = new FileOutputStream(outputFileName);
        decompress(inputFile, outputFile);
        inputFile.close();
        outputFile.flush();
        outputFile.close();
    }


    /**
     * 数据解压缩
     *
     * @param is
     * @param os
     */
    public static void decompress(InputStream is, OutputStream os) {
        GZIPInputStream gis = null;
        try {
            gis = new GZIPInputStream(is);
            int count;
            byte data[] = new byte[1024];
            while ((count = gis.read(data, 0, 1024)) != -1) {
                os.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (gis != null) {
                    gis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 文件解压缩
     */
    public static void unTarGz(String source, String dest) {
//        decompress(dest, false);
        unTar(new File(source), new File(dest));
    }

    /**
     * 文件解压缩
     */
    public static void unTarGz(File source, File dest) {
//        decompress(dest, false);
        unTar(source, dest);
    }

    /**
     * 文件解压缩
     *
     * @param path
     * @param delete 是否删除原始文件
     */
    public static void decompress(String path, boolean delete) {
        File file = new File(path);
        decompress(file, delete);
    }
}
