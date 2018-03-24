package challenge.models.hackerNews

import challenge.models.hackerNews.User.Id

case class User(id: Id, storyCommentsCount: Int, totalCommentsCount: Int)

object User {
  type Id = String
}