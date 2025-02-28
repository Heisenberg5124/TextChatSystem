USE [master]
GO
/****** Object:  Database [TextChatSystem]    Script Date: 7/23/2020 12:26:24 PM ******/
CREATE DATABASE [TextChatSystem]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'TextChatSystem', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL14.SQLEXPRESS\MSSQL\DATA\TextChatSystem.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'TextChatSystem_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL14.SQLEXPRESS\MSSQL\DATA\TextChatSystem_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
GO
ALTER DATABASE [TextChatSystem] SET COMPATIBILITY_LEVEL = 140
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [TextChatSystem].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [TextChatSystem] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [TextChatSystem] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [TextChatSystem] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [TextChatSystem] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [TextChatSystem] SET ARITHABORT OFF 
GO
ALTER DATABASE [TextChatSystem] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [TextChatSystem] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [TextChatSystem] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [TextChatSystem] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [TextChatSystem] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [TextChatSystem] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [TextChatSystem] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [TextChatSystem] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [TextChatSystem] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [TextChatSystem] SET  DISABLE_BROKER 
GO
ALTER DATABASE [TextChatSystem] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [TextChatSystem] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [TextChatSystem] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [TextChatSystem] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [TextChatSystem] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [TextChatSystem] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [TextChatSystem] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [TextChatSystem] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [TextChatSystem] SET  MULTI_USER 
GO
ALTER DATABASE [TextChatSystem] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [TextChatSystem] SET DB_CHAINING OFF 
GO
ALTER DATABASE [TextChatSystem] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [TextChatSystem] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [TextChatSystem] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [TextChatSystem] SET QUERY_STORE = OFF
GO
USE [TextChatSystem]
GO
/****** Object:  Table [dbo].[Message]    Script Date: 7/23/2020 12:26:24 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Message](
	[id] [nvarchar](10) NOT NULL,
	[sender] [nvarchar](20) NOT NULL,
	[recipient] [nvarchar](20) NOT NULL,
	[message_content] [text] NOT NULL,
	[create_time] [datetime] NOT NULL,
	[is_read] [bit] NOT NULL,
 CONSTRAINT [PK_Message] PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[User]    Script Date: 7/23/2020 12:26:24 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[User](
	[username] [nvarchar](20) NOT NULL,
	[password] [nvarchar](20) NOT NULL,
	[fullname] [nvarchar](50) NOT NULL,
	[online] [bit] NOT NULL,
 CONSTRAINT [PK_User] PRIMARY KEY CLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ__User__F3DBC57203C271CA] UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
 CONSTRAINT [UQ__User__F3DBC5727C0DF8AF] UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Message] ADD  CONSTRAINT [DF_create_time]  DEFAULT (getdate()) FOR [create_time]
GO
ALTER TABLE [dbo].[User] ADD  CONSTRAINT [DF_User_online]  DEFAULT ((0)) FOR [online]
GO
ALTER TABLE [dbo].[Message]  WITH CHECK ADD  CONSTRAINT [FK_Message_User] FOREIGN KEY([sender])
REFERENCES [dbo].[User] ([username])
GO
ALTER TABLE [dbo].[Message] CHECK CONSTRAINT [FK_Message_User]
GO
ALTER TABLE [dbo].[Message]  WITH CHECK ADD  CONSTRAINT [FK_Message_User1] FOREIGN KEY([recipient])
REFERENCES [dbo].[User] ([username])
GO
ALTER TABLE [dbo].[Message] CHECK CONSTRAINT [FK_Message_User1]
GO
/****** Object:  StoredProcedure [dbo].[InsertMessage]    Script Date: 7/23/2020 12:26:24 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create procedure [dbo].[InsertMessage]
    @sender nvarchar(20),
    @recipient nvarchar(20),
    @body text,
    @isRead bit
as
begin
    declare @num_message int,
        @message_id nvarchar(10);
    select @num_message = count(*) from [Message];
    set @num_message = @num_message + 1;
    set @message_id = ('M' + (select right('000' + cast(@num_message as nvarchar(3)), 3)));
    insert into Message (id, sender, recipient, message_content, is_read) values (@message_id, @sender, @recipient, @body, @isRead);
end
GO
USE [master]
GO
ALTER DATABASE [TextChatSystem] SET  READ_WRITE 
GO
