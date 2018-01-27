<div class="modal fade" id="adminEventEditModal" role="dialog">
    <div class="modal-dialog modal-dialog-centered">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Edit Event Details</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<form>
				    <div class="form-group">
				        <label for="recipient-name" class="col-form-label">Title:</label>
				        <input type="text" class="form-control" id="eventTitle">
				    </div>
				    <div class="form-group">
				        <label for="message-text" class="col-form-label">Start Time:</label>
				        <input type="datetime-local" class="form-control" id="eventSTime">
				    </div>
				    <div class="form-group">
				        <label for="message-text" class="col-form-label">End Time:</label>
				        <input type="datetime-local" class="form-control" id="eventETime">
				    </div>
				    <div class="form-group">
				        <label for="message-text" class="col-form-label">Location:</label>
				        <input type="text" class="form-control" id="eventLocation">
				    </div>
				    <input type="hidden" id="eventId" value="0">
				</form>
            </div>
            <div class="modal-footer">
            	<button onclick="editEvent();" type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>


<div class="modal fade" id="partiListModal" role="dialog">
    <div class="modal-dialog modal-xl modal-dialog-centered">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Participants List</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
            	<table class="table table-striped table-bordered">
				    <thead class="thead-default">
				        <tr>
				            <th>Name</th>
				            <th>Email</th>
				            <th>Registered at</th>
				        </tr>
				    </thead>
				    <tbody>
				        <tr>
			                <th>${user.name}</th>
			                <th>${user.username}</th>
			            </tr>
			            <tr>
			                <th>${user.name}</th>
			                <th>${user.username}</th>
			            </tr>
				    </tbody>
				</table>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>