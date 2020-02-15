<!--通过js动态添加-->
                                <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                                     th:each="comment:${comments}">
                                    <div class="media">
                                        <div class="media-left">
                                            <a href="#">
                                                <img class="media-object img-rounded"
                                                     th:src="${comment.user.avatarUrl}">
                                            </a>
                                        </div>
                                        <div class="media-body">
                                            <h5 class="media-heading" th:text="${comment.user.name}">

                                            </h5>
                                            <div th:text="${comment.content}">回复内容</div>
                                            <div class="menu">
                                                <span class="pull-right"
                                                      th:text="${#dates.format(comment.gmtCreate,'HH: mm dd/MM/yyyy')}"></span>
                                            </div>
                                        </div>
                                    </div>
                                </div>