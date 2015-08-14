package nl.knaw.dans.easy

import java.io.File

import com.yourmediashelf.fedora.client.FedoraCredentials

import scala.util.{Success, Failure, Try}

package object fsrdb {
  val homedir = new File(System.getenv("EASY_UPDATE_FS_RDB_HOME"))


  case class Settings(fedoraCredentials: FedoraCredentials,
                      postgresURL: String,
                      datasetPid: String)

  abstract class Item(val pid: String,
                               val parentSid: String,
                               val datasetSid: String,
                               val path: String)

  case class FolderItem(override val pid: String,
                        override val parentSid: String,
                        override val datasetSid: String,
                        override val path: String,
                        name: String) extends Item(pid, parentSid, datasetSid, path)

  case class FileItem(override val pid: String,
                      override val parentSid: String,
                      override val datasetSid: String,
                      override val path: String,
                      filename: String,
                      size: Int,
                      mimetype: String,
                      creatorRole: String,
                      visibleTo: String,
                      accessibleTo: String,
                      sha1checksum: String) extends Item(pid, parentSid, datasetSid, path)

  class CompositeException(throwables: List[Throwable]) extends RuntimeException(throwables.foldLeft("")((msg, t) => s"$msg\n${t.getMessage}"))

  implicit class ListTryExtensions[T](xs: List[Try[T]]) {
    def sequence: Try[List[T]] =
      if (xs.exists(_.isFailure))
        Failure(new CompositeException(xs.collect { case Failure(e) => e }))
      else
        Success(xs.map(_.get))
  }
}