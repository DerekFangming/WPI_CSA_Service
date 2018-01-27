<div class="modal fade" id="downloadTicketModal" role="dialog">
    <div class="modal-dialog modal-dialog-centered">

        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title">Already purchased</h4>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <p>You have already paid for it. Do you want to download ticket again?</p>
                <input type="hidden" id="ticketId" value="">
            </div>
            <form action="/action_page.php" method="get" style="display: none;">
            </form>
            <div class="modal-footer">
            	<button id="downloadTicketButton" type="button" class="btn btn-primary" data-dismiss="modal">Yes</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
            </div>
        </div>

    </div>
</div>