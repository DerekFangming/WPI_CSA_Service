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
            	<button id="updateEventBtn" onclick="editEvent();" type="button" class="btn btn-primary" data-dismiss="modal">Save</button>
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
            	<table id="partiTable" class="table table-striped table-bordered table-hover" cellspacing="0" width="100%">
				    <thead>
				        <tr>
				            <th>Name</th>
				            <th>Email</th>
				            <th>Registered at</th>
				        </tr>
				    </thead>
				    <tbody id="partiList">
				    </tbody>
				</table>
				<div id="partiLoading" class="col" align="center">
				</div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>