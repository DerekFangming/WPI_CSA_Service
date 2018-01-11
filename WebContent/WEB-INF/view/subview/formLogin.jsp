<!-- Modal -->
<div class="modal fade bs-modal-sm" id="loginModal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-md">
        <div class="modal-content">
            <br>
            <ul id="myTab" class="nav nav-tabs">
                <li class="nav-item"><a class="nav-link active" href="#signin" data-toggle="tab">Sign In</a></li>
                <li class="nav-item"><a class="nav-link" href="#signup" data-toggle="tab">Register</a></li>
                <li class="nav-item"><a class="nav-link" href="#why" data-toggle="tab">Why?</a></li>
            </ul>
            <div class="modal-body">
                <div id="myTabContent" class="tab-content">
                    <div class="tab-pane fade" id="why">
                        <p>You need to log in to get free tickets for events hosted by CSA, edit Survival Guide, etc. Note that you need to register using your @wpi.edu email in order to have such privilege.</p>
                        <p><br> Please contact <a href="mailto:csa@wpi.edu">csa@wpi.edu</a> for any questions.</p>
                    </div>
                    <div class="tab-pane fade show active" id="signin">
                        <form id="loginForm" class="form-horizontal">
                            <fieldset>
                                <!-- Sign In Form -->
                                <!-- Text input-->
                                <div class="control-group">
                                    <label class="control-label" for="userid">Username:</label>
                                    <div class="controls">
                                        <input required="" id="username" name="username" type="text" class="form-control" placeholder="Your username \ email" class="input-medium" required="">
                                    </div>
                                </div>

                                <!-- Password input-->
                                <div class="control-group">
                                    <label class="control-label" for="passwordinput">Password:</label>
                                    <div class="controls">
                                        <input required="" id="password" name="password" class="form-control" type="password" placeholder="Your password" class="input-medium">
                                    </div>
                                </div>

                                <!-- Multiple Checkboxes (inline) -->
                                <div class="control-group">
                                    <label class="control-label" for="rememberme"></label>
                                    <div class="controls">
                                        <label class="checkbox inline" for="rememberme-0">
											<input type="checkbox" name="remember" id="remember" value="remember" checked>
											Remember me
											</label>
                                    </div>
                                </div>
                                
                                <input type="hidden" name="redirect" value="${redirectPage}"/>
                                
                                <!-- Button -->
                                <div class="control-group">
                                    <label class="control-label" for="signin"></label>
                                    <div class="controls">
                                        <button id="signinButton" name="signin" class="btn btn-success">Sign In</button>
                                    </div>
                                </div>
                            </fieldset>
                        </form>
                    </div>
                    <div class="tab-pane fade" id="signup">
                        <form class="form-horizontal">
                            <fieldset>
                                <!-- Sign Up Form -->
                                <!-- Text input-->
                                <div class="control-group">
                                    <label class="control-label" for="Email">Username:</label>
                                    <div class="controls">
                                        <input id="Email" name="Email" class="form-control" type="text" placeholder="Your email. Preferably your @wpi.edu email" class="input-large" required="">
                                    </div>
                                </div>

                                <!-- Text input-->
                                <div class="control-group">
                                    <label class="control-label" for="userid">Name:</label>
                                    <div class="controls">
                                        <input id="userid" name="userid" class="form-control" type="text" placeholder="Your name" class="input-large" required="">
                                    </div>
                                </div>

                                <!-- Password input-->
                                <div class="control-group">
                                    <label class="control-label" for="password">Password:</label>
                                    <div class="controls">
                                        <input id="signuppassword" name="signuppassword" class="form-control" type="password" placeholder="At least 6 characters with letters and numbers" class="input-large" required="">

                                    </div>
                                </div>

                                <!-- Text input-->
                                <div class="control-group">
                                    <label class="control-label" for="reenterpassword">Re-Enter Password:</label>
                                    <div class="controls">
                                        <input id="reenterpassword" class="form-control" name="reenterpassword" type="password" placeholder="Enter your password again" class="input-large" required="">
                                    </div>
                                </div>

                                <!-- Button -->
                                <div class="control-group">
                                    <label class="control-label" for="confirmsignup"></label>
                                    <div class="controls">
                                        <button id="confirmsignup" name="confirmsignup" class="btn btn-success">Sign Up</button>
                                    </div>
                                </div>
                            </fieldset>
                        </form>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <center>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </center>
            </div>
        </div>
    </div>
</div>