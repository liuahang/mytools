package com.ahang.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class SFtpUtils {

	private static final int TIMEOUT = 6000;

	/**
	 * @����: ��¼SFTP������
	 * @����: yangc
	 * @��������: 2013-11-21 ����03:31:51
	 * @param ipAddress IP��ַ(192.168.112.128)
	 * @param port �˿�(21)
	 * @param username �û���(root)
	 * @param password ����(123456)
	 */
	public ChannelSftp login(String ipAddress, int port, String username, String password) {
		JSch jsch = new JSch();
		try {
			Session session = jsch.getSession(username, ipAddress, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setTimeout(TIMEOUT);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			return (ChannelSftp) channel;
		} catch (JSchException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @����: �ǳ�SFTP������
	 * @����: yangc
	 * @��������: 2013-11-21 ����03:31:51
	 */
	public void logout(ChannelSftp sftp) {
		if (sftp != null) {
			sftp.quit();
			sftp.disconnect();
		}
	}

	/**
	 * @����: ��ȡָ��·���µ��ļ����б�
	 * @����: yangc
	 * @��������: 2013-11-21 ����03:31:51
	 */
	public List<String> getFileNameList(ChannelSftp sftp, String path) {
		if (sftp == null || sftp.isClosed()) {
			throw new IllegalArgumentException("ChannelSftp has bean closed!");
		}

		List<String> fileNameList = new ArrayList<String>();
		try {
			Vector<?> vector = sftp.ls(path);
			for (int i = 0; i < vector.size(); i++) {
				LsEntry entry = (LsEntry) vector.get(i);
				fileNameList.add(entry.getFilename());
			}
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return fileNameList;
	}

	/**
	 * @����: ��ָ��Ŀ¼�´���Ŀ¼
	 * @����: yangc
	 * @��������: 2013-11-21 ����02:25:10
	 * @param path ���ĸ�Ŀ¼�´���
	 * @param dirName Ҫ������Ŀ¼����
	 */
	public boolean mkDir(ChannelSftp sftp, String path, String dirName) {
		if (sftp == null || sftp.isClosed()) {
			throw new IllegalArgumentException("ChannelSftp has bean closed!");
		}

		try {
			sftp.cd(path);
			sftp.mkdir(dirName);
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @����: ��ָ��Ŀ¼��ɾ���ļ�
	 * @����: yangc
	 * @��������: 2013-11-21 ����02:25:10
	 * @param path ���ĸ�Ŀ¼��ɾ��
	 * @param fileName Ҫɾ�����ļ�����
	 */
	public boolean deleteFile(ChannelSftp sftp, String path, String fileName) {
		if (sftp == null || sftp.isClosed()) {
			throw new IllegalArgumentException("ChannelSftp has bean closed!");
		}

		try {
			sftp.cd(path);
			sftp.rm(fileName);
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @����: ��SFTP�������ϴ��ļ�
	 * @����: yangc
	 * @��������: 2013-11-21 ����02:25:10
	 * @param file Ҫ�ϴ����ļ�
	 * @param path �ϴ�·��(/var/ftp/pub/)
	 * @param monitor ���Ȼص�
	 */
	public boolean upload(ChannelSftp sftp, File file, String path, SftpProgressMonitor monitor) {
		return this.upload(sftp, Arrays.asList(file), path, monitor);
	}

	/**
	 * @����: ��SFTP�������ϴ��ļ�
	 * @����: yangc
	 * @��������: 2013-11-21 ����02:25:10
	 * @param files Ҫ�ϴ����ļ�
	 * @param path �ϴ�·��(/var/ftp/pub/)
	 * @param monitor ���Ȼص�
	 */
	public boolean upload(ChannelSftp sftp, List<File> files, String path, SftpProgressMonitor monitor) {
		if (sftp == null || sftp.isClosed()) {
			throw new IllegalArgumentException("ChannelSftp has bean closed!");
		}

		BufferedInputStream bis = null;
		try {
			for (File file : files) {
				bis = new BufferedInputStream(new FileInputStream(file));
				sftp.put(bis, path, monitor);
				bis.close();
				bis = null;
			}
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * @����: ��SFTP�����������ļ�
	 * @����: yangc
	 * @��������: 2013-11-21 ����03:31:51
	 * @param fileName Ҫ���ص��ļ���("test_1.txt")
	 * @param srcPath FTP�������ļ���·��(/var/ftp/pub/)
	 * @param destPath ���غ󱣴��·��(E:/workspace/utils/)
	 * @param monitor ���Ȼص�
	 */
	public boolean download(ChannelSftp sftp, String fileName, String srcPath, String destPath, SftpProgressMonitor monitor) {
		return this.download(sftp, Arrays.asList(fileName), srcPath, destPath, monitor);
	}

	/**
	 * @����: ��SFTP�����������ļ�
	 * @����: yangc
	 * @��������: 2013-11-21 ����03:31:51
	 * @param fileNames Ҫ���ص��ļ���(Arrays.asList("test_1.txt", "test_2.txt");)
	 * @param srcPath FTP�������ļ���·��(/var/ftp/pub/)
	 * @param destPath ���غ󱣴��·��(E:/workspace/utils/)
	 * @param monitor ���Ȼص�
	 */
	public boolean download(ChannelSftp sftp, List<String> fileNames, String srcPath, String destPath, SftpProgressMonitor monitor) {
		if (sftp == null || sftp.isClosed()) {
			throw new IllegalArgumentException("ChannelSftp has bean closed!");
		}

		BufferedOutputStream bos = null;
		try {
			for (String fileName : fileNames) {
				bos = new BufferedOutputStream(new FileOutputStream(destPath + "/" + fileName));
				sftp.get(srcPath + "/" + fileName, bos, monitor);
				bos.close();
				bos = null;
			}
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bos != null) bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
